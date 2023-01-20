package com.prgrms.be.intermark.domain.schedule.dtos;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Builder
public record ScheduleCreateRequestDTO(
        @NotNull long musicalId,
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startTime
) {

    public LocalDateTime toLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(time, formatter);
    }

    public LocalDateTime getStartTime() {
        return toLocalDateTime(this.startTime);
    }

    public LocalDateTime getEndTime(Musical musical) {
        return getStartTime().plusMinutes(musical.getRunningTime());
    }

    public Schedule toEntity(Musical musical) {
        return Schedule.builder()
                .startTime(getStartTime())
                .endTime(getEndTime(musical))
                .isDeleted(false)
                .musical(musical)
                .build();
    }
}
