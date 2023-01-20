package com.prgrms.be.intermark.domain.ticket.controller;

import com.prgrms.be.intermark.domain.ticket.dto.TicketFindResponseDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketFindResponseDTOs;
import org.springframework.web.bind.annotation.*;
import com.prgrms.be.intermark.domain.ticket.dto.TicketDeleteResponseDto;
import lombok.RequiredArgsConstructor;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
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

	@DeleteMapping("/{ticketId}")
	public ResponseEntity<TicketDeleteResponseDto> deleteTicket(@PathVariable Long ticketId) {
		TicketDeleteResponseDto deleteResponseDto = ticketService.deleteTicket(ticketId);
		return ResponseEntity.ok(deleteResponseDto);
	}

	@GetMapping
	public ResponseEntity<TicketFindResponseDTOs> getAllTicket() {
		TicketFindResponseDTOs tickets = ticketService.getAllTicket();
		return ResponseEntity.ok(tickets);
	}

	@GetMapping("/{ticketId}")
	public ResponseEntity<TicketFindResponseDTO> getTicketById(@PathVariable Long ticketId) {
		TicketFindResponseDTO ticket = ticketService.getTicketById(ticketId);
		return ResponseEntity.ok(ticket);
	}

}