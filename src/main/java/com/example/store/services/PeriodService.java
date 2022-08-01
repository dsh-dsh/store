package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Period;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeriodService {

    @Autowired
    private PeriodRepository periodRepository;

    public Period getCurrentPeriod() {
        return periodRepository.findByIsCurrent(true)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_PERIOD_MESSAGE));
    }

    @Transactional
    public Period setNextPeriod() {
        Period current = getCurrentPeriod();
        Period next = new Period();
        next.setStartDate(current.getEndDate().plusDays(1));
        next.setEndDate(next.getStartDate().plusMonths(1).minusDays(1));
        current.setCurrent(false);
        next.setCurrent(true);
        periodRepository.save(current);
        periodRepository.save(next);
        return next;
    }

}
