package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.DocItemMapper;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.DocItemRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DocItemService {

    @Autowired
    protected DocItemRepository docItemRepository;
    @Autowired
    protected ItemService itemService;
    @Autowired
    private DocItemMapper docItemMapper;

    // TODO add tests

    public void addDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);
    }

    public DocumentItem createNewDocItem(DocItemDTO docItemDTO) {
        DocumentItem item = new DocumentItem();
        item.setItem(itemService.getItemById(docItemDTO.getItemId()));
        item.setQuantity(docItemDTO.getQuantity());
        item.setQuantityFact(docItemDTO.getQuantityFact());
        item.setPrice(docItemDTO.getPrice());
        item.setDiscount(docItemDTO.getDiscount());

        return item;
    }

    protected DocumentItem createDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem item = new DocumentItem();
        item.setItemDoc((ItemDoc) doc);
        item.setItem(itemService.getItemById(docItemDTO.getItemId()));
        item.setQuantity(docItemDTO.getQuantity());
        item.setQuantityFact(docItemDTO.getQuantityFact());
        item.setPrice(docItemDTO.getPrice());
        item.setDiscount(docItemDTO.getDiscount());

        return item;
    }

    public void updateDocItems(List<DocItemDTO> docItemDTOList, ItemDoc doc) {
        List<DocumentItem> currentDocumentItems = getItemsByDoc(doc);
        List<Integer> ids = new ArrayList<>();
        for(DocumentItem currentDocumentItem : currentDocumentItems) {
            for(DocItemDTO docItemDTO : docItemDTOList) {
                if(Objects.equals(docItemDTO.getItemId(), currentDocumentItem.getItem().getId())) {
                    updateDocItem(currentDocumentItem, docItemDTO);
                    ids.add(docItemDTO.getItemId());
                }
            }
        }
        currentDocumentItems.stream()
                .filter(documentItem -> !ids.contains(documentItem.getItem().getId()))
                .forEach(documentItem -> docItemRepository.delete(documentItem));
        docItemDTOList.stream()
                .filter(dto -> !ids.contains(dto.getItemId()))
                .map(dto -> createDocItem(dto, doc))
                .forEach(documentItem -> docItemRepository.save(documentItem));
    }

    public void updateDocItem(DocumentItem item, DocItemDTO dto) {
        item.setQuantity(dto.getQuantity());
        item.setQuantityFact(dto.getQuantityFact());
        item.setPrice(dto.getPrice());
        item.setDiscount(dto.getDiscount());
        docItemRepository.save(item);
    }

    public DocumentItem getItemById(int id) {
        return docItemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_ITEM_MESSAGE));
    }

    public List<DocumentItem> getItemsByDoc(ItemDoc doc) {
        return docItemRepository.findByItemDoc(doc);
    }

    public List<DocItemDTO> getItemDTOListByDoc(ItemDoc doc) {
        List<DocumentItem> items = getItemsByDoc(doc);
        return items.stream().map(docItemMapper::mapToDocItemDTO).collect(Collectors.toList());
    }

    public void deleteByDoc(ItemDoc itemDoc) {
        docItemRepository.deleteByItemDoc(itemDoc);
    }

    public void save(DocumentItem docItem) {
        docItemRepository.save(docItem);
    }
}

