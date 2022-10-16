package com.example.store.services;

import com.example.store.model.entities.PropertySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class SchedulerService {

    @Autowired
    private Hold1CDocksService hold1CDocksService;
    @Autowired
    @Qualifier("checkHoldingEnable")
    private PropertySetting checkHoldingEnableSetting;

    @Scheduled(cron = "${hold.docs.scheduling.cron.expression}")
    public void holdChecksForADay() {
        if(checkHoldingEnableSetting.getProperty() == 0) return;
        LocalDateTime to = LocalDate.now(ZoneId.systemDefault()).atStartOfDay();
        LocalDateTime from = to.minusDays(1);
        hold1CDocksService.hold1CDocsByPeriod(from, to);
    }
}
