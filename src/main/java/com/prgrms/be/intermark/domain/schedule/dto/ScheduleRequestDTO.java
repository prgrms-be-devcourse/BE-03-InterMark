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

    public LocalDateTime getStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(this.startTime, formatter);
    }

    public Schedule toEntity(PerformanceStadium performanceStadium) {
        return Schedule.builder()
                .startTime(getStartTime())
                .performanceStadium(performanceStadium)
                .build();
    }
}
