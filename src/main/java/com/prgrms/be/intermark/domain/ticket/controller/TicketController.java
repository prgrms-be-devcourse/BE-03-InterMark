package com.prgrms.be.intermark.domain.ticket.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketDeleteResponseDto;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/tickets")
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
}
