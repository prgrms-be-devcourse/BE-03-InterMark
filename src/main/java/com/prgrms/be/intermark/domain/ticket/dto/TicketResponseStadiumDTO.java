package com.prgrms.be.intermark.domain.ticket.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record TicketResponseStadiumDTO(
        @NotNull String name,
        @NotNull String address) {
}
