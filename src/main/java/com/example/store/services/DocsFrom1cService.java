package com.example.store.services;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Setter
@Service
public class DocsFrom1cService {

    @Autowired
    private DocItemServiceFor1CDocs docItemServiceFor1CDocs;
    @Autowired
    private CheckInfoServiceFor1CDock checkInfoServiceFor1CDock;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected StorageService storageService;
    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected ItemDocRepository itemDocRepository;
    @Autowired
    protected DocumentRepository documentRepository;
    @Autowired
    protected OrderDocRepository orderDocRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;

    private LocalDateTime docDateTime;

    @Value("${spring.mail.to.email}")
    private String toEmail;

    public void addDocument(DocDTO docDTO) {
        DocumentType docType = DocumentType.getByValue(docDTO.getDocType());
        if(isDocNumberExists(docDTO.getNumber(), docType)) return;

        Document document;
        if(docType == DocumentType.CHECK_DOC || docType == DocumentType.INVENTORY_DOC) {
            document = new ItemDoc();
        } else {
            document = new OrderDoc();
        }

        document.setNumber(docDTO.getNumber());
        document.setDateTime(getNewTime(Util.getLocalDate(docDTO.getDate())));
        document.setProject(projectService.getByName(docDTO.getProject().getName()));
        document.setAuthor(userService.getByName(docDTO.getAuthor().getName()));
        document.setSupplier(companyService.getByInn(docDTO.getSupplier().getInn()));
        document.setDocType(docType);

        if(docType == DocumentType.CHECK_DOC) {
            ItemDoc itemDoc = (ItemDoc) document;
            itemDoc.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
            itemDocRepository.save(itemDoc);
            checkInfoServiceFor1CDock.addCheckInfo(docDTO.getCheckInfo(), itemDoc);
            addDocItems(docDTO, itemDoc);
        } else if(docType == DocumentType.INVENTORY_DOC) {
            ItemDoc itemDoc = (ItemDoc) document;
            itemDoc.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
            itemDocRepository.save(itemDoc);
            addDocItems(docDTO, itemDoc);
        } else {
            OrderDoc orderDoc = (OrderDoc) document;
            orderDoc.setAmount(docDTO.getAmount());
            orderDoc.setTax(docDTO.getTax());
            orderDoc.setIndividual(userService.getByCode(docDTO.getIndividual().getCode()));
            orderDoc.setPaymentType(PaymentType.getByValue(docDTO.getPaymentType()));
            orderDocRepository.save((OrderDoc) document);
        }
    }

    protected void addDocItems(DocDTO docDTO, ItemDoc check) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemServiceFor1CDocs.addDocItem(docItemDTO, check));
    }

    public boolean isDocNumberExists(long number, DocumentType type) {
        LocalDateTime startOfYear = LocalDate.now().withMonth(1).withDayOfMonth(1).atStartOfDay();
        boolean exists = documentRepository.existsByNumberAndDocTypeAndDateTimeAfter(number, type, startOfYear);
        if(exists) {
            mailService.send(toEmail, Constants.MESSAGE_SUBJECT, String.format(Constants.DOC_NUMBER_EXISTS_MESSAGE, type.getValue(), number));
        }
        return exists;
    }

    protected LocalDateTime getNewTime(LocalDate docDate) {
        if(this.docDateTime != null
                && !docDate.equals(this.docDateTime.toLocalDate())) {
            this.docDateTime = null;
        }
        if(this.docDateTime == null) this.docDateTime = getLastDocTime(docDate);
        this.docDateTime = this.docDateTime.plus(1, ChronoUnit.MILLIS); // todo update tests
        return this.docDateTime;
    }

    protected LocalDateTime getLastDocTime(LocalDate docDate) {
        LocalDateTime start = docDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Optional<Document> optionalDocument =
                documentRepository.findLastCheckDocOfDay(blockingUserIds, start, end);
        if(optionalDocument.isPresent()) {
            return optionalDocument.get().getDateTime();
        } else {
            return start.plusHours(Constants.START_HOUR_1C_DOCS).minus(1, ChronoUnit.MILLIS);
        }
    }

}
