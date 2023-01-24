package com.prgrms.be.intermark.domain.ticket.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record TicketResponseSeatDTO(
        @NotNull String grade,
        @NotNull String seatNum) {
}
