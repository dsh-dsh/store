package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.springframework.stereotype.Service;

@Service
public class DocItemServiceFor1CDocs extends DocItemService {

    @Override
    public void addDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);
    }

    @Override
    protected DocumentItem createDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem item = new DocumentItem();
        item.setItemDoc((ItemDoc) doc);
        item.setItem(itemService.findItemByNumber(docItemDTO.getItemId())
                .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_CODE_ITEM_MESSAGE, docItemDTO.getItemId()))));
        item.setQuantity(Util.floorValue(docItemDTO.getQuantity(), 3));
        item.setQuantityFact(Util.floorValue(docItemDTO.getQuantityFact(), 3));
        item.setPrice(docItemDTO.getPrice());
        item.setDiscount(docItemDTO.getDiscount());

        return item;
    }
}
