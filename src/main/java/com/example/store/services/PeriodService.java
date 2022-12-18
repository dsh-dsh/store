package com.example.store.services;

import com.example.store.components.PeriodDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.PeriodDTO;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class  PeriodService {

    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private HoldDocsService holdDocService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private User systemUser;
    @Autowired
    private PeriodDateTime periodDateTime;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;


    @Transactional
    public PeriodDTO closePeriodManually() {
        closePeriod();
        return getPeriodDTO();
    }

    @Transactional
    public void closePeriodTransactional() {
        closePeriod();
    }

    public void closePeriod() {
        long docTimeMillis = 0;
        checkPossibilityToClosePeriod();
        LocalDateTime newPeriodStart = getNewPeriodStart();
        List<Storage> storages = storageService.getStorageList();
        for (Storage storage : storages) {
            closePeriodForStorage(newPeriodStart.plus(docTimeMillis++, ChronoUnit.MILLIS), storage);
        }
        setNextPeriod();
    }

    protected void checkPossibilityToClosePeriod() {
        Period currentPeriod = getCurrentPeriod();
        List<Document> notHoldenDocs = documentRepository.findByIsHoldAndIsDeletedAndDateTimeBetween(
                false, false,
                currentPeriod.getStartDate().atStartOfDay(),
                currentPeriod.getEndDate().plusDays(1).atStartOfDay(),
                Sort.by(Constants.DATE_TIME_STRING));
        if(!notHoldenDocs.isEmpty()) {
            throw new BadRequestException(
                    Constants.NOT_HOLDEN_DOCS_IN_PERIOD_MESSAGE,
                    this.getClass().getName() + " - checkPossibilityToClosePeriod()");
        }
        checkingExistingHoldenDocsAfter(currentPeriod);
    }

    protected void checkingExistingHoldenDocsAfter(Period currentPeriod) {
        if(documentRepository.existsByDateTimeAfterAndIsHold(
                currentPeriod.getEndDate().plusDays(1).atStartOfDay(), true)) {
            throw new BadRequestException(
                    Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE,
                    this.getClass().getName() + " - checkingExistingHoldenDocsAfter(Period currentPeriod)");
        }
    }

    public void closePeriodForStorage(LocalDateTime newPeriodStart, Storage storage) {
        ItemDoc doc = createRestMoveDoc(newPeriodStart, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForClosingPeriod(storage, doc.getDateTime());
        if(itemRestMap.size() > 0) {
            documentRepository.save(doc);
            List<DocumentItem> items = getDocItems(doc, itemRestMap); // todo refactor it
            items.forEach(item -> docItemService.save(item));
            holdDocService.holdDoc(doc);
        }
    }

    public List<DocumentItem> getDocItems(ItemDoc document, Map<Item, ItemRestService.RestPriceValue> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().getRest().compareTo(BigDecimal.ZERO) > 0)
                .map(entry -> new DocumentItem(document, entry.getKey(),
                        entry.getValue().getRest(),
                        entry.getValue().getPrice()))
                .collect(Collectors.toList());
    }

    protected ItemDoc createRestMoveDoc(LocalDateTime newPeriodStart, Storage storage) {
        ItemDoc doc = new ItemDoc();
        doc.setDocType(DocumentType.PERIOD_REST_MOVE_DOC);
        doc.setNumber(documentService.getNextDocumentNumber(DocumentType.PERIOD_REST_MOVE_DOC));
        doc.setDateTime(newPeriodStart);
        doc.setStorageTo(storage);
        Project project = projectService.getProjectByStorageName(storage.getName())
                .orElseGet(() -> projectService.getById(Constants.EMPTY_PROJECT_ID));
        doc.setProject(project);
        doc.setAuthor(systemUser);
        doc.setRecipient(companyService.getOurCompany());
        return doc;
    }

    public LocalDate getStartDateByDateInPeriod(LocalDate date) {
        return periodRepository.findStartDateByDateInPeriod(date)
                .orElse(LocalDate.parse(Constants.DEFAULT_PERIOD_START));
    }

    public Period getCurrentPeriod() {
        return periodRepository.findByIsCurrent(true)
                .orElseGet(() -> {
                    Period period = new Period();
                    period.setStartDate(LocalDate.parse(Constants.DEFAULT_PERIOD_START));
                    period.setEndDate(LocalDate.now().plusDays(30).atStartOfDay().toLocalDate());
                    return period;
                });
    }

    LocalDateTime getNewPeriodStart() {
        Period current = getCurrentPeriod();
        return current.getEndDate().plusDays(1).atStartOfDay();
    }

    public Period setNextPeriod() {
        Period current = getCurrentPeriod();
        Period next = getNextPeriod(current);
        if(current != null) {
            current.setCurrent(false);
            periodRepository.save(current);
        }
        next.setCurrent(true);
        periodRepository.save(next);
        periodDateTime.setPeriodStart();
        return next;
    }

    @NotNull
    private Period getNextPeriod(Period current) {
        LocalDate startDate = current != null? current.getEndDate().plusDays(1) : LocalDate.now();
        Period next = new Period();
        next.setStartDate(startDate);
        next.setEndDate(next.getStartDate().plusMonths(1).minusDays(1));
        return next;
    }

    public PeriodDTO getPeriodDTO() {
        Period period = getCurrentPeriod();
        PeriodDTO dto = new PeriodDTO();
        LocalDate start = period != null? period.getStartDate() : LocalDate.parse(Constants.DEFAULT_PERIOD_START);
        LocalDate end = period != null? period.getEndDate() : LocalDate.now().plusMonths(1).withDayOfMonth(1);
        dto.setStartDate(Util.getLongLocalDate(start));
        dto.setEndDate(Util.getLongLocalDate(end));
        return dto;
    }

    public Long getBlockTime() {
        List<User> authors = blockingUserIds.stream()
                .map(userService::getById).collect(Collectors.toList());
        Document document = documentRepository
                .findFirstByAuthorInAndIsHold(authors, true, Sort.by(Constants.DATE_TIME_STRING).descending())
                .orElse(null);
        return document != null?
                Util.getLongLocalDateTime(document.getDateTime()) :
                Util.getLongLocalDateTime(LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay());
    }
}
