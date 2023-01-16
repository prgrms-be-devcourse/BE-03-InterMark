package com.prgrms.be.intermark.domain.ticket.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TicketFindResponseDTOs(
        List<TicketFindResponseDTO> ticketFindResponseDTOs
) {
}
