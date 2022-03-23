package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.factories.itemdoc.*;
import com.example.sklad.mappers.DocMapper;
import com.example.sklad.mappers.DocToListMapper;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.dto.documents.DocToListDTO;
import com.example.sklad.model.entities.documents.Document;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.DocumentRepository;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private CheckDocFactory checkDocFactory;
    @Autowired
    private ReceiptDocFactory receiptDocFactory;
    @Autowired
    private PostingDocFactory postingDocFactory;
    @Autowired
    private RequestDocFactory requestDocFactory;
    @Autowired
    private InventoryDocFactory inventoryDocFactory;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private DocToListMapper docToListMapper;

    public void addCheckDoc(DocDTO docDTO) {
        checkDocFactory.addDocument(docDTO);
    }

    public void updateCheckDoc(DocDTO docDTO) {
        checkDocFactory.updateDocument(docDTO);
    }

    public void addReceiptDoc(DocDTO docDTO) {
        receiptDocFactory.addDocument(docDTO);
    }

    public void updateReceiptDoc(DocDTO docDTO) {
        receiptDocFactory.updateDocument(docDTO);
    }

    public void addPostingDoc(DocDTO docDTO) {
        postingDocFactory.addDocument(docDTO);
    }

    public void addInventoryDoc(DocDTO docDTO) {
        inventoryDocFactory.addDocument(docDTO);
    }

    public void updatePostingDoc(DocDTO docDTO) {
        postingDocFactory.updateDocument(docDTO);
    }

    public void addRequestDoc(DocDTO docDTO) {
        requestDocFactory.addDocument(docDTO);
    }

    public void updateRequestDoc(DocDTO docDTO) {
        requestDocFactory.updateDocument(docDTO);
    }

    public void updateInventoryDoc(DocDTO docDTO) {
        inventoryDocFactory.updateDocument(docDTO);
    }

    public ItemDoc getDocumentByNumber(int number) {
        return itemDocRepository.findByNumber(number)
                .orElseThrow(BadRequestException::new);
    }

    public List<DocToListDTO> getDocumentsByType(DocumentType documentType) {
        List<Document> docs = documentRepository.getByDocType(documentType);
        return docs.stream()
                .peek(document -> System.out.println(document.getId()))
                .map(docToListMapper::mapToDocDTO)
                .collect(Collectors.toList());
    }

    public List<ItemDoc> getItemDocsByType(DocumentType documentType) {
        return itemDocRepository.findByDocType(documentType);
    }

    public ItemDoc getDocumentById(int docId) {
        return itemDocRepository.findById(docId)
                .orElseThrow(BadRequestException::new);
    }

    public DocDTO getDocDTOById(int docId) {
        ItemDoc itemDoc = itemDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        return docMapper.mapToDocDTO(itemDoc);
    }

    public void deleteDocument(DocDTO docDTO) {
        int docId = docDTO.getId();
        inventoryDocFactory.deleteDocument(docId);
    }
}
