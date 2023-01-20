package com.prgrms.be.intermark.domain.schedule.dtos;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Builder
public record ScheduleCreateRequestDTO(
        @NotNull long musicalId,
        @NotNull String startTime
) {

    public LocalDateTime toLocalDateTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(time, formatter);
        } catch (Exception e) {
            throw new IllegalStateException("잘못된 날짜 시간 형식입니다.");
        }
    }

    public Schedule toEntity(Musical musical) {
        LocalDateTime startTime = toLocalDateTime(this.startTime);
        LocalDateTime endTime = startTime.plusMinutes(musical.getRunningTime());

        return Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .isDeleted(false)
                .musical(musical)
                .build();
    }
}
