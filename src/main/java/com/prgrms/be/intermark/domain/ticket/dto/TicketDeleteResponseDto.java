package com.prgrms.be.intermark.domain.ticket.dto;

import com.prgrms.be.intermark.domain.ticket.TicketStatus;

import lombok.Builder;

@Builder
public record TicketDeleteResponseDto(Long ticketId, TicketStatus status, String message) {
}
