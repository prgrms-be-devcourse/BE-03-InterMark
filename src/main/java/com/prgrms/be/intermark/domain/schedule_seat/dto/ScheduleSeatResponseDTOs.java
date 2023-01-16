package com.prgrms.be.intermark.domain.schedule_seat.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record ScheduleSeatResponseDTOs(List<ScheduleSeatResponseDTO> scheduleSeatResponseDTOs) {
}
