package com.prgrms.be.intermark.domain.ticket.controller;

import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
@RestController
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Long> reservePerformance(@RequestBody @Valid TicketCreateRequestDTO ticketCreateRequestDTO) {
        Long ticketId = ticketService.createTicket(ticketCreateRequestDTO);

        return ResponseEntity
                .created(URI.create("/tickets/" + ticketId))
                .body(ticketId);
    }
}
