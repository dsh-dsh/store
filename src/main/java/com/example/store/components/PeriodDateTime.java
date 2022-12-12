package com.example.store.components;

import com.example.store.model.entities.Period;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PeriodDateTime {

    @Autowired
    private PeriodRepository periodRepository;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @PostConstruct
    public void setPeriodStart() {
        Optional<Period> period = periodRepository.findByIsCurrent(true);
        this.startDateTime = period.map(value -> value.getStartDate().atStartOfDay())
                .orElseGet(() -> LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay());
        this.endDateTime = period.map(value -> value.getEndDate().plusDays(1).atStartOfDay()) // set to start of next day after period
                .orElseGet(() -> LocalDate.now().plusDays(30).atStartOfDay());
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    // for tests only
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
