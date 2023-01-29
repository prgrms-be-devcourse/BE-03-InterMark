package com.prgrms.be.intermark.domain.schedule.dto;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
public record ScheduleUpdateRequestDTO(@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startTime) {

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

}
