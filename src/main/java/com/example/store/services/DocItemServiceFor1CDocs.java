package com.example.store.services;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import org.springframework.stereotype.Service;

@Service
public class DocItemServiceFor1CDocs extends DocItemService {

    // TODO add tests

    @Override
    public void addDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);
    }

    @Override
    protected DocumentItem createDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem item = new DocumentItem();
        item.setItemDoc((ItemDoc) doc);
        // из 1с item_id приходит с номером, не id
        int id = itemService.getItemIdByNumber(docItemDTO.getItemId());
        item.setItem(itemService.getItemById(id));
        item.setQuantity(docItemDTO.getQuantity());
        item.setQuantityFact(docItemDTO.getQuantityFact());
        item.setPrice(docItemDTO.getPrice());
        item.setDiscount(docItemDTO.getDiscount());

        return item;
    }
}
