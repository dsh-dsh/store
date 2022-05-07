package com.example.store.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SchedulerServiceTest {

    @InjectMocks
    private SchedulerService schedulerService;
    @Mock
    private SchedulerService schedulerServiceMocked;

//    @Test
//    void holdChecksForADayTest() {
//        LocalDateTime to = LocalDateTime.now(ZoneId.systemDefault());
//        LocalDateTime from = to.minusDays(1);
//        schedulerServiceMocked.holdChecksForADay();
//        verify(schedulerServiceMocked, times(1)).hold1CDocsByPeriod(from, to);
//    }

}
