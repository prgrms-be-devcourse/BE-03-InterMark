package com.prgrms.be.intermark.domain.ticket.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
public record TicketResponseStadiumDTO(
        @NotNull String name,
        @NotNull String address) {
}
