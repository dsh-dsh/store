package com.example.store.services;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Project;
import com.example.store.model.reports.SalesItemLine;
import com.example.store.model.reports.SalesReport;
import com.example.store.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SalesReportService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ItemService itemService;

    private List<DocumentItem> documentItems;
    private boolean includeNull;
    private boolean onlyHolden;

    // todo add tests

    public SalesReport getSalesReport(int itemId, int projectId, long dateStart, long dateEnd, boolean includeNull, boolean onlyHolden) {
        Project project = projectService.getById(projectId);
        LocalDateTime start = Util.getLocalDate(dateStart).atStartOfDay();
        LocalDateTime end = Util.getLocalDate(dateEnd).atStartOfDay().plusDays(1);
        this.includeNull = includeNull;
        this.onlyHolden = onlyHolden;
        Item item = getItem(itemId);
        return getReport(item, project, start, end);
    }

    public SalesReport getReport(Item item, Project project, LocalDateTime dateStart, LocalDateTime dateEnd) {
        List<Item> items = getItemList(item);
        documentItems = docItemService.getItemsByPeriod(project, dateStart, dateEnd, onlyHolden);
        List<SalesItemLine> lines = items.stream()
                .map(this::getSalesItemLine)
                .filter(this::isEmpty)
                .collect(Collectors.toList());
        return new SalesReport(lines);
    }

    private boolean isEmpty(SalesItemLine line) {
        return includeNull || line.getQuantity().compareTo(BigDecimal.ZERO) > 0;
    }

    @NotNull
    private SalesItemLine getSalesItemLine(Item lineItem) {
        Supplier<Stream<DocumentItem>> supplier = () -> documentItems.stream()
                .filter(docItem -> docItem.getItem().equals(lineItem));
        BigDecimal quantity = supplier.get()
                .map(DocumentItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal amount = supplier.get()
                .map(this::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalesItemLine(lineItem.getId(), lineItem.getName(), lineItem.getUnit().getValue(), quantity, amount);
    }

    private BigDecimal getAmount(DocumentItem docItem) {
        return docItem.getQuantity()
                .multiply(BigDecimal.valueOf(docItem.getPrice()))
                .subtract(BigDecimal.valueOf(docItem.getDiscount()));
    }

    @Nullable
    private Item getItem(int itemId) {
        Item item = null;
        if(itemId > 0) {
            item = itemService.getItemById(itemId);
        }
        return item;
    }

    public List<Item> getItemList(Item item) {
        if(item == null) {
            return itemService.getAllItems();
        } else if(item.isNode()) {
            return itemService.getByParent(item);
        } else {
            return List.of(item);
        }
    }
}
