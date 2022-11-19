package com.example.store.controllers;

import com.example.store.model.reports.ItemMovesReport;
import com.example.store.model.reports.PeriodReport;
import com.example.store.model.responses.Response;
import com.example.store.services.ItemMovesReportService;
import com.example.store.services.PeriodReportService;
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

    // todo add tests

    @GetMapping("/period")
    public ResponseEntity<Response<PeriodReport>> getPeriodReport(
            @RequestParam int projectId, @RequestParam long start, @RequestParam long end){
        return ResponseEntity.ok(new Response<>(periodReportService.getPeriodReport(projectId, start, end)));
    }

    @GetMapping("/item/moves")
    public ResponseEntity<Response<ItemMovesReport>> getItemMovesReport(
            @RequestParam int storageId, @RequestParam long start, @RequestParam long end,
            @RequestParam boolean includeNull, @RequestParam boolean onlyHolden){
        return ResponseEntity.ok(new Response<>(itemMovesReportService
                .getItemMoveReport(storageId, start, end, includeNull, onlyHolden)));
    }

}
