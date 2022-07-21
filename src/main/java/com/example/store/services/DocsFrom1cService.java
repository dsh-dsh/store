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
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transaction
    public void addDocument(DocDTO docDTO) {
        if(isDocNumberExists(docDTO.getNumber())) return;

        DocumentType docType = DocumentType.getByValue(docDTO.getDocType());
        Document document;
        if(docType == DocumentType.CHECK_DOC) {
            document = new ItemDoc();
        } else {
            document = new OrderDoc();
        }
        document.setNumber(docDTO.getNumber());
        document.setDateTime(getNewTime(document, docDTO));
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

    public boolean isDocNumberExists(int number) {
        return documentRepository.existsByNumberAndDateTimeAfter(number, startOfYear);
    }

    public LocalDateTime getNewTime(Document document, DocDTO dto) {
        LocalDateTime newTime = LocalDateTime.parse(dto.getDate(), Constants.TIME_FORMATTER);
        LocalDateTime start = newTime;
        LocalDateTime end = start.plusDays(1);
        // todo consider on if it is the same document
        if(document.getDateTime() == null || (document.getDateTime() != null && !document.getDateTime().equals(newTime))) {
            Optional<Document> optionalDocument = documentRepository.getFirstByDateTimeBetweenOrderByDateTimeDesc(start, end);
            if(optionalDocument.isPresent()) {
                newTime = optionalDocument.get().getDateTime().plus(1, ChronoUnit.MILLIS);
            }
        }
        return newTime;
    }

}
