package com.example.store.services;

import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class SchedulerService {

    @Autowired
    private Hold1CDocksService hold1CDocksService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;

    // TODO add tests

    @Scheduled(cron = "${hold.docs.scheduling.cron.expression}")
    public void holdChecksForADay() {
        LocalDateTime to = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime from = to.minusDays(1);
        hold1CDocsByPeriod(from, to);
    }

    public void hold1CDocsByPeriod(LocalDateTime from, LocalDateTime to) {
        List<Storage> storages = storageService.getStorageList();
        storages.forEach(storage -> {
            hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, from, to);
            hold1CDocksService.createSaleOrders(storage, from);
            hold1CDocksService.holdDocsAndChecksByStoragesAndPeriod(storage, from, to);
        });
        List<Project> projects = projectService.getProjectList();
        projects.forEach(project -> hold1CDocksService.holdOrdersByProjectsAndPeriod(project, from, to));
    }
}
