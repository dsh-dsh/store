package com.example.store.controllers;

import com.example.store.model.reports.ItemMovesReport;
import com.example.store.model.reports.PeriodReport;
import com.example.store.model.reports.SalesReport;
import com.example.store.model.responses.Response;
import com.example.store.services.reports.ItemMovesReportService;
import com.example.store.services.reports.PeriodReportService;
import com.example.store.services.reports.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reports")
public class ReportController {

    @Autowired
    private PeriodReportService periodReportService;
    @Autowired
    private ItemMovesReportService itemMovesReportService;
    @Autowired
    private SalesReportService salesReportService;

    // todo add tests

    @GetMapping("/period")
    public ResponseEntity<Response<PeriodReport>> getPeriodReport(
            @RequestParam int projectId, @RequestParam long start, @RequestParam long end){
        return ResponseEntity.ok(new Response<>(periodReportService.getPeriodReport(projectId, start, end)));
    }

    @GetMapping("/item/moves")
    public ResponseEntity<Response<ItemMovesReport>> getItemMovesReport(
            @RequestParam int itemId, @RequestParam int storageId, @RequestParam long start, @RequestParam long end,
            @RequestParam boolean includeNull, @RequestParam boolean onlyHolden){
        return ResponseEntity.ok(new Response<>(itemMovesReportService
                .getItemMoveReport(itemId, storageId, start, end, includeNull, onlyHolden)));
    }

    @GetMapping("/sales")
    public ResponseEntity<Response<SalesReport>> getSalesReport(
            @RequestParam int itemId, @RequestParam int projectId, @RequestParam long start, @RequestParam long end,
            @RequestParam boolean includeNull, @RequestParam boolean onlyHolden){
        return ResponseEntity.ok(new Response<>(salesReportService
                .getSalesReport(itemId, projectId, start, end, includeNull, onlyHolden)));
    }

}
