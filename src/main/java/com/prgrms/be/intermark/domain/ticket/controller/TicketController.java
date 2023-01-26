package com.prgrms.be.intermark.domain.ticket.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByMusicalDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByUserDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Void> createTicket(@RequestBody @Valid TicketCreateRequestDTO ticketCreateRequestDTO) {

        Long ticketId = ticketService.createTicket(ticketCreateRequestDTO);

        return ResponseEntity.created(
            URI.create("/api/v1/tickets/" + ticketId)
        ).build();
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<Ticket, TicketResponseDTO>> getAllTickets(Pageable pageable) {
        PageResponseDTO<Ticket, TicketResponseDTO> tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<PageResponseDTO<Ticket, TicketResponseByUserDTO>> getTicketsByUser(
            @PathVariable("userId") Long userId, Pageable pageable) {
        PageResponseDTO<Ticket, TicketResponseByUserDTO> tickets = ticketService.getTicketsByUser(userId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/musicals/{musicalId}")
    public ResponseEntity<PageResponseDTO<Ticket, TicketResponseByMusicalDTO>> getTicketsByMusical(
            @PathVariable("musicalId") Long musicalId, Pageable pageable) {
        PageResponseDTO<Ticket, TicketResponseByMusicalDTO> tickets = ticketService.getTicketsByMusical(
                musicalId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable("ticketId") Long ticketId) {
        TicketResponseDTO ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);

        return ResponseEntity.noContent().build();
    }
}
