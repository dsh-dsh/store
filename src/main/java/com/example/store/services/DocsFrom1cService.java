package com.example.store.services;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

    private static final LocalDateTime startOfYear = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
    private LocalDateTime docDateTime;

    @Transaction // set transaction to all docs, not only one
    public void addDocument(DocDTO docDTO) {
        DocumentType docType = DocumentType.getByValue(docDTO.getDocType());
        if(isDocNumberExists(docDTO.getNumber(), docType)) return;

        Document document;
        if(docType == DocumentType.CHECK_DOC) {
            document = new ItemDoc();
        } else {
            document = new OrderDoc();
        }
        document.setNumber(docDTO.getNumber());
        document.setDateTime(getNewTime(Util.getLocalDate(docDTO.getDate())));
        document.setProject(projectService.getByName(docDTO.getProject().getName()));
        document.setAuthor(userService.getByName(docDTO.getAuthor().getName()));
        document.setSupplier(companyService.getByName(docDTO.getSupplier().getName()));
        document.setIndividual(userService.getByName(docDTO.getIndividual().getName()));
        document.setDocType(docType);
        if(docType == DocumentType.CHECK_DOC) {
            ItemDoc itemDoc = (ItemDoc) document;
            itemDoc.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
            itemDocRepository.save(itemDoc);
            checkInfoServiceFor1CDock.addCheckInfo(docDTO.getCheckInfo(), itemDoc);
            addDocItems(docDTO, itemDoc);
        } else {
            OrderDoc orderDoc = (OrderDoc) document;
            orderDoc.setAmount(docDTO.getAmount());
            orderDoc.setTax(docDTO.getTax());
            orderDocRepository.save((OrderDoc) document);
        }
    }

    private void addDocItems(DocDTO docDTO, ItemDoc check) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemServiceFor1CDocs.addDocItem(docItemDTO, check));
    }

    public boolean isDocNumberExists(long number, DocumentType type) {
        return documentRepository.existsByNumberAndDocTypeAndDateTimeAfter(number, type, startOfYear);
    }

    // todo tests
    protected LocalDateTime getNewTime(LocalDate docDate) {
        if(this.docDateTime == null) {
            this.docDateTime = getLastDocTime(docDate).plus(1, ChronoUnit.MILLIS);
        } else {
            this.docDateTime = this.docDateTime.plus(1, ChronoUnit.MILLIS);
        }
        return this.docDateTime;
    }

    // todo tests
    private LocalDateTime getLastDocTime(LocalDate docDate) {
        LocalDateTime start = docDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Optional<Document> optionalDocument =
                documentRepository.getFirstByDateTimeBetween(start, end, Sort.by(Constants.DATE_TIME_STRING).descending());
        if(optionalDocument.isPresent()) {
            return optionalDocument.get().getDateTime();
        } else {
            return start.plusHours(1);
        }
    }

}
