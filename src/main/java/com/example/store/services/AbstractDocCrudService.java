package com.example.store.services;

import com.example.store.components.ReHoldChecking;
import com.example.store.components.UnHoldDocs;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.repositories.ProjectRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class AbstractDocCrudService {

    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected DocItemService docItemService;
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
    protected CheckInfoService checkInfoService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ReHoldChecking reHoldChecking;
    @Autowired
    private LotService lotService;
    @Autowired
    private UnHoldDocs unHoldDocs;

    protected DocumentType documentType;
    protected DocDTO docDTO;

    public DocInterface addItemDoc(DocDTO docDTO) {
        ItemDoc itemDoc = (ItemDoc) setDocument(getOrAddItemDoc(docDTO));
        setAdditionalFieldsAndSave(itemDoc);
        addDocumentItems(itemDoc, docDTO);
        if(docDTO.getDocType().equals(DocumentType.CHECK_DOC.getValue())) {
            addCheckInfo(itemDoc, docDTO);
        }
        return itemDoc;
    }

    public DocInterface addOrderDoc(DocDTO docDTO) {
        OrderDoc order = (OrderDoc) setDocument(getOrAddOrderDoc(docDTO));
        setAdditionalFieldsAndSave(order);
        return order;
    }

    @Transaction
    public DocInterface updateItemDoc(DocDTO docDTO) {
        ItemDoc itemDoc = (ItemDoc) setDocument(getOrAddItemDoc(docDTO));
        boolean reHoldPossible = reHoldChecking.checkPossibility(itemDoc, docDTO);
        setAdditionalFieldsAndSave(itemDoc);
        updateDocItems(itemDoc);
        if(reHoldPossible) {
            lotService.updateLotMovements(itemDoc);
        } else {
            unHoldDocs.unHoldAllDocsAfter(itemDoc);
        }
        updateCheckInfo(itemDoc);
        return itemDoc;
    }

    public DocInterface updateOrderDocument(DocDTO docDTO) {
        return addOrderDoc(docDTO);
//        OrderDoc order = (OrderDoc) setDocument(getOrAddOrderDoc(docDTO));
//        setAdditionalFieldsAndSave(order);
//        return order;
    }

    public void deleteItemDoc(int docId) {
        ItemDoc itemDoc = itemDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        unHoldDocs.unHoldAllDocsAfter(itemDoc);
        itemDoc.setDeleted(!itemDoc.isDeleted());
        itemDocRepository.save(itemDoc);
    }

    public void deleteOrderDoc(int docId) {
        OrderDoc order = orderDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        // TODO ?????? ???????????? ???????????????? ???????? ?????????? ????????????????, ???? ?????????????????? ???????? ???? ???? ?????????????????????? ???? ???????? ??????????????????
        order.setDeleted(!order.isDeleted());
        orderDocRepository.save(order);
    }

    protected Document setDocument(Document document) {
        DocumentType docType = DocumentType.getByValue(docDTO.getDocType());
        setDocumentType(docType);
        setCommonFields(document, docDTO);
        return document;
    }

    protected ItemDoc getOrAddItemDoc(DocDTO docDTO) {
        setDocDTO(docDTO);
        int docId = docDTO.getId();
        if(docId != 0) {
            return itemDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        } else {
            return new ItemDoc();
        }
    }

    protected OrderDoc getOrAddOrderDoc(DocDTO docDTO) {
        setDocDTO(docDTO);
        int docId = docDTO.getId();
        if(docId != 0) {
            return orderDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        } else {
            return new OrderDoc();
        }
    }

    protected void setDocDTO(DocDTO dto) {
        this.docDTO = dto;
    }

    protected void setAdditionalFieldsAndSave(Document document) {
        if (docDTO.getIndividual().getId() != 0) {
            document.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        }
        if (docDTO.getSupplier().getId() != 0) {
            document.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        }
        if (docDTO.getRecipient().getId() != 0) {
            document.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        }
        if (document instanceof ItemDoc) {
            ItemDoc itemDoc = (ItemDoc) document;
                if (docDTO.getStorageTo().getId() != 0) {
                    itemDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
                }
            if (docDTO.getStorageFrom().getId() != 0) {
                itemDoc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
            }
            itemDocRepository.save(itemDoc);
        } else {
            orderDocRepository.save((OrderDoc) document);
        }
    }

    protected void addDocumentItems(ItemDoc document, DocDTO docDTO) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, document));
    }

    protected void addCheckInfo(ItemDoc check, DocDTO docDTO) {
        checkInfoService.addCheckInfo(docDTO.getCheckInfo(), check);
    }

    protected void setCommonFields(Document document, DocDTO docDTO) {
        if(docDTO.getNumber() == 0) {
            document.setNumber(getNextDocumentNumber());
        } else {
            document.setNumber(docDTO.getNumber());
        }
        document.setDocType(documentType);
        document.setDateTime(getNewTime(document, docDTO));
        document.setProject(projectService.getById(docDTO.getProject().getId()));
        document.setAuthor(userService.getById(docDTO.getAuthor().getId()));
        document.setPayed(docDTO.isPayed());
        document.setHold(docDTO.isHold());
        setBaseDocument(document, docDTO);
    }

    protected void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    protected LocalDateTime getNewTime(Document document, DocDTO dto) {
        LocalDateTime newTime = Instant.ofEpochMilli(dto.getDateTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime start = LocalDateTime.of(newTime.getYear(), newTime.getMonth(), newTime.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = start.plusDays(1);
        // todo consider on if it is the same document
        if(document.getDateTime() == null || (document.getDateTime() != null && !document.getDateTime().equals(newTime))) {
            Optional<Document> optionalDocument = documentRepository.getFirstByDateTimeBetweenOrderByDateTimeDesc(start, end);
            if(optionalDocument.isPresent()) {
                newTime = optionalDocument.get().getDateTime().plus(1, ChronoUnit.MILLIS);
            } else {
                newTime = start;
            }
        }
        return newTime;
    }

    protected int getNextDocumentNumber() {
        try {
            return documentRepository.getLastNumber(documentType.toString()) + 1;
        } catch (Exception exception) {
            return Constants.START_DOCUMENT_NUMBER;
        }
    }

    protected void setBaseDocument(Document document, DocDTO docDTO) {
        if(docDTO.getBaseDocumentId() > 0) {
            Document baseDoc = documentRepository.findById(docDTO.getBaseDocumentId())
                    .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_BASE_DOCUMENT_MESSAGE));
            document.setBaseDocument(baseDoc);
        }
    }

    protected void updateDocItems(ItemDoc document) {
        docItemService.updateDocItems(docDTO.getDocItems(), document);
    }

    protected void updateCheckInfo(ItemDoc check) {
        if(docDTO.getDocType().equals(DocumentType.CHECK_DOC.getValue())) {
            checkInfoService.updateCheckInfo(docDTO.getCheckInfo(), check);
        }
    }
}
