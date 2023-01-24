package com.prgrms.be.intermark.domain.ticket.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
public record TicketResponseSeatDTO(
        @NotNull String grade,
        @NotNull String seatNum) {
}
