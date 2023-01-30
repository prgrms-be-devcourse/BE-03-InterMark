package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;

import java.time.LocalDateTime;

public class ScheduleProvider {

    public static Schedule createSchedule(Musical musical) {
        return Schedule.builder()
                .musical(musical)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(5))
                .build();
    }

}
