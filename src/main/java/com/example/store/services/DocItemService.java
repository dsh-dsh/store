package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.DocItemMapper;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.DocItemRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    public void addDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem documentItem = createDocItem(docItemDTO, doc);
        docItemRepository.save(documentItem);
    }

    protected DocumentItem createDocItem(DocItemDTO docItemDTO, Document doc) {
        DocumentItem item = new DocumentItem();
        item.setItemDoc((ItemDoc) doc);
        item.setItem(itemService.findItemById(docItemDTO.getItemId()));
        item.setQuantity(BigDecimal.valueOf(docItemDTO.getQuantity()).setScale(3, RoundingMode.HALF_EVEN));
        item.setQuantityFact(Util.floorValue(docItemDTO.getQuantityFact(), 3));
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
        item.setQuantity(BigDecimal.valueOf(dto.getQuantity()).setScale(3, RoundingMode.HALF_EVEN));
        item.setQuantityFact(Util.floorValue(dto.getQuantityFact(), 3));
        item.setPrice(dto.getPrice());
        item.setDiscount(dto.getDiscount());
        docItemRepository.save(item);
    }

    public DocumentItem getItemById(int id) {
        return docItemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_DOCUMENT_ITEM_MESSAGE,
                        this.getClass().getName() + " - getItemById(int id)"));
    }

    public List<DocumentItem> getItemsByDoc(ItemDoc doc) {
        return docItemRepository.findByItemDoc(doc);
    }

    public List<DocumentItem> getDocItemsByItem(
            Item item, Storage storage, LocalDateTime start, LocalDateTime end, boolean onlyHolden) {
        return docItemRepository.findByItemId(item.getId(), storage.getId(), start, end, onlyHolden);
    }

    public List<DocItemDTO> getItemDTOListByDoc(ItemDoc doc) {
        List<DocumentItem> items = getItemsByDoc(doc);
        return items.stream().map(docItemMapper::mapToDocItemDTO).collect(Collectors.toList());
    }

    public Float getItemsAmount(ItemDoc itemDoc) {
        List<DocumentItem> items = getItemsByDoc(itemDoc);
        return (float) items.stream()
                .mapToDouble(item -> Util.floorValue((item.getQuantity().floatValue() * item.getPrice()) - item.getDiscount(), 2))
                .sum();
    }

    public void deleteByDocs(List<Document> documents) {
        documents.stream()
                .filter(ItemDoc.class::isInstance)
                .forEach(doc -> deleteByDoc((ItemDoc) doc));
    }

    public void deleteByDoc(ItemDoc itemDoc) {
        docItemRepository.deleteByItemDoc(itemDoc);
    }

    public void save(DocumentItem docItem) {
        docItemRepository.save(docItem);
    }

    public List<DocumentItem> getItemsByPeriod(Project project, LocalDateTime start, LocalDateTime end, boolean onlyHolden) {
        return docItemRepository.findByPeriod(project.getId(), start, end, onlyHolden);
    }
}

