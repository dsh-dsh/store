package com.example.sklad.services;

import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.repositories.DocItemRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DocItemService {

    @Autowired
    private DocItemRepository docItemRepository;

    @Autowired
    private ItemService itemService;

    public void addDocItem(DocItemDTO docItemDTO, ItemDoc doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);

    }

    @NotNull
    private DocumentItem createDocItem(DocItemDTO docItemDTO, ItemDoc doc) {
        DocumentItem documentItem = new DocumentItem();
        documentItem.setItemDoc(doc);
        documentItem.setItem(itemService.getItemById(docItemDTO.getItemId()));
        documentItem.setQuantity(docItemDTO.getQuantity());
        documentItem.setPrice(docItemDTO.getPrice());
        documentItem.setDiscount(docItemDTO.getDiscount());

        return documentItem;
    }

    public void updateDocItems(List<DocItemDTO> docItemDTOList, ItemDoc doc) {
        List<DocumentItem> currentItems = docItemRepository.findByItemDoc(doc);
        List<Long> ids = new ArrayList<>();
        for(DocumentItem currentItem : currentItems) {
            for(DocItemDTO dto : docItemDTOList) {
                if(Objects.equals(dto.getItemId(), currentItem.getId())) {
                    updateDocItem(currentItem, dto);
                    ids.add(dto.getItemId());
                }
            }
        }
        currentItems.stream()
                .filter(item -> !ids.contains(item.getItem().getId()))
                .forEach(item -> docItemRepository.delete(item));
        docItemDTOList.stream()
                .filter(dto -> !ids.contains(dto.getItemId()))
                .map(dto -> createDocItem(dto, doc))
                .forEach(item -> docItemRepository.save(item));
    }

    private void updateDocItem(DocumentItem docItem, DocItemDTO dto) {
        docItem.setQuantity(dto.getQuantity());
        docItem.setPrice(dto.getPrice());
        docItem.setDiscount(dto.getDiscount());
        docItemRepository.save(docItem);
    }
}

