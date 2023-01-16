package com.prgrms.be.intermark.domain.schedule_seat.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record ScheduleSeatFindRequestDTO(@NotNull Long scheduleId) {
}
