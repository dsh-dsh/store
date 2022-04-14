package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.ItemDocFactory;
import com.example.store.mappers.DocMapper;
import com.example.store.mappers.DocToListMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.responses.ListResponse;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private ItemDocFactory itemDocFactory;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private DocToListMapper docToListMapper;

    public void addDocument(DocDTO docDTO) {
        itemDocFactory.addDocument(docDTO);
    }

    public void updateDocument(DocDTO docDTO) {
        itemDocFactory.updateDocument(docDTO);
    }

    public ItemDoc getDocumentByNumber(int number) {
        return itemDocRepository.findByNumber(number)
                .orElseThrow(BadRequestException::new);
    }

    public List<ItemDoc> getDocumentsByTypeAndStorageAndIsHold(DocumentType type, Storage storage, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return itemDocRepository.findByDocTypeAndStorageFromAndIsHoldAndDateTimeBetween(type, storage, isHold, from, to);
    }

    public List<OrderDoc> getDocumentsByTypeInAndProjectAndIsHold(List<DocumentType> types, Project project, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return orderDocRepository.findByDocTypeInAndProjectAndIsHoldAndDateTimeBetween(types, project, isHold, from, to);
    }

    public ListResponse<DocToListDTO> getDocumentsByType(DocumentType documentType, Pageable pageable) {
        Page<Document> page = documentRepository.getByDocType(documentType, pageable);
        List<DocToListDTO> dtoList = page.stream()
                .map(docToListMapper::mapToDocDTO)
                .collect(Collectors.toList());
        return new ListResponse<>(dtoList, page);
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

    public void softDeleteDocument(DocDTO docDTO) {
        int docId = docDTO.getId();
        itemDocFactory.deleteDocument(docId);
    }

    public void setItemDocHolden(ItemDoc doc) {
        doc.setHold(true);
        itemDocRepository.save(doc);
    }
}
