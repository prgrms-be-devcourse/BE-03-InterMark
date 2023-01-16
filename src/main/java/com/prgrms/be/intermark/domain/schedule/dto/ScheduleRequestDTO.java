package com.prgrms.be.intermark.domain.schedule.dto;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule.Schedule;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
public record ScheduleRequestDTO(
        @NotNull String startTime,
        @NotNull Long performanceId,
        @NotNull Long stadiumId
) {
    public Schedule toEntity(PerformanceStadium performanceStadium) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime scheduleTime = LocalDateTime.parse(this.startTime, formatter);

        return Schedule.builder()
                .startTime(scheduleTime)
                .performanceStadium(performanceStadium)
                .build();
    }
}
