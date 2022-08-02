package com.example.store.services;

import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PeriodService {

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

    @Transactional
    public void closePeriod() {
        Period period = setNextPeriod();
        List<Storage> storages = storageService.getStorageList();
        storages.forEach(storage -> closePeriodForStorage(period, storage));
    }

    public void closePeriodForStorage(Period period, Storage storage) {
        ItemDoc doc = createRestMoveDoc(period, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForPeriod(storage, doc.getDateTime());
        if(itemRestMap.size() > 0) {
            documentRepository.save(doc);
            List<DocumentItem> items = getDocItems(doc, itemRestMap);
            items.forEach(item -> docItemService.save(item));
            holdDocService.holdDocument(doc);
        }
    }

    public List<DocumentItem> getDocItems(ItemDoc document, Map<Item, ItemRestService.RestPriceValue> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().getRest() > 0)
                .map(entry -> new DocumentItem(document, entry.getKey(),
                        entry.getValue().getRest(), entry.getValue().getPrice()))
                .collect(Collectors.toList());
    }

    protected ItemDoc createRestMoveDoc(Period period, Storage storage) {
        ItemDoc doc = new ItemDoc();
        doc.setDocType(DocumentType.PERIOD_REST_MOVE_DOC);
        doc.setNumber(documentService.getNextDocumentNumber(DocumentType.PERIOD_REST_MOVE_DOC));
        doc.setDateTime(period.getStartDate().atStartOfDay());
        doc.setStorageTo(storage);
        Project project = projectService.getProjectByStorageName(storage.getName())
                .orElseGet(() -> projectService.getById(Constants.EMPTY_PROJECT_ID));
        doc.setProject(project);
        doc.setAuthor(userService.getSystemAuthor());
        doc.setRecipient(companyService.getOurCompany());
        return doc;
    }


    public Period getCurrentPeriod() {
        return periodRepository.findByIsCurrent(true).orElse(null);
    }

    @Transactional
    public Period setNextPeriod() {
        Period current = getCurrentPeriod();
        Period next = getNextPeriod(current);
        if(current != null) {
            current.setCurrent(false);
            periodRepository.save(current);
        }
        next.setCurrent(true);
        periodRepository.save(next);
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

}
