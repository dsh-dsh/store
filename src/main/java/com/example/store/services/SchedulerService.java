package com.example.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class SchedulerService {

    @Autowired
    private Hold1CDocksService hold1CDocksService;

    // TODO test
    @Scheduled(cron = "${hold.docs.scheduling.cron.expression}")
    public void holdChecksForADay() {
        LocalDateTime to = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime from = to.minusDays(1);
        hold1CDocksService.hold1CDocsByPeriod(from, to);
    }
}
