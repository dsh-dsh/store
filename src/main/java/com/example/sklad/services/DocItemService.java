package com.example.sklad.services;

import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.repositories.DocItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocItemService {

    @Autowired
    private DocItemRepository docItemRepository;

    @Autowired
    private ItemService itemService;

    public DocumentItem addDocItem(DocItemDTO docItemDTO, ItemDoc doc) {

        DocumentItem documentItem = new DocumentItem();

        documentItem.setItemDoc(doc);
        documentItem.setItem(itemService.getItemById(docItemDTO.getItemId()));
        documentItem.setQuantity(docItemDTO.getQuantity());
        documentItem.setPrice(docItemDTO.getPrice());

        docItemRepository.save(documentItem);

        return documentItem;

    }

}
