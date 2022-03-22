package com.example.sklad.services;

import com.example.sklad.mappers.DocItemMapper;
import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.Document;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.repositories.DocItemRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DocItemService {

    @Autowired
    private DocItemRepository docItemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private DocItemMapper docItemMapper;

    public void addDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);
    }

    @NotNull
    private DocumentItem createDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem item = new DocumentItem();
        item.setItemDoc((ItemDoc) doc);
        item.setItem(itemService.getItemById(docItemDTO.getItemId()));
        item.setQuantity(docItemDTO.getQuantity());
        float qf = docItemDTO.getQuantityFact();
        item.setQuantityFact(docItemDTO.getQuantityFact());
        item.setPrice(docItemDTO.getPrice());
        item.setDiscount(docItemDTO.getDiscount());

        return item;
    }

    public void updateDocItems(List<DocItemDTO> docItemDTOList, ItemDoc doc) {
        List<DocumentItem> currentItems = getItemsByDoc(doc);
        List<Integer> ids = new ArrayList<>();
        for(DocumentItem currentItem : currentItems) {
            for(DocItemDTO dto : docItemDTOList) {
                if(Objects.equals(dto.getItemId(), currentItem.getItem().getId())) {
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
        System.out.println();
    }

    private void updateDocItem(DocumentItem item, DocItemDTO dto) {
        item.setQuantity(dto.getQuantity());
        item.setQuantityFact(dto.getQuantityFact());
        item.setPrice(dto.getPrice());
        item.setDiscount(dto.getDiscount());
        docItemRepository.save(item);
    }

    public List<DocumentItem> getItemsByDoc(ItemDoc doc) {
        return docItemRepository.findByItemDoc(doc);
    }

    public DocItemDTO getItemDTO(DocumentItem item) {
        return docItemMapper.mapToDocItemDTO(item);
    }

    public List<DocItemDTO> getItemDTOListByDoc(ItemDoc doc) {
        List<DocumentItem> items = getItemsByDoc(doc);
        return items.stream().map(docItemMapper::mapToDocItemDTO).collect(Collectors.toList());
    }

    public int countItemsByDoc(int docId) {
        return docItemRepository.countItemsByDocId(docId);
    }

    public void deleteByDoc(ItemDoc itemDoc) {
        docItemRepository.deleteByItemDoc(itemDoc);
    }
}

