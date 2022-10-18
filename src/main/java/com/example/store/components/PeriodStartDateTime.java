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
public class PeriodStartDateTime {

    @Autowired
    private PeriodRepository periodRepository;

    private LocalDateTime dateTime;

    @PostConstruct
    public void setPeriodStart() {
        Optional<Period> period = periodRepository.findByIsCurrent(true);
        this.dateTime = period.map(value -> value.getStartDate().atStartOfDay())
                .orElseGet(() -> LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay());
    }

    public LocalDateTime get() {
        return dateTime;
    }
}
