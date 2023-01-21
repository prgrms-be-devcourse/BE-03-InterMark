package com.prgrms.be.intermark.domain.schedule_seat.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatFindRequestDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;
import com.prgrms.be.intermark.domain.schedule_seat.service.ScheduleSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seats")
public class ScheduleSeatApiController {

	private final ScheduleSeatService scheduleSeatService;

	@GetMapping
	public ResponseEntity<ScheduleSeatResponseDTOs> getScheduleSeats(
		@RequestBody @Valid ScheduleSeatFindRequestDTO findRequestDTO
	) {
		ScheduleSeatResponseDTOs scheduleSeatDTOs = scheduleSeatService.getScheduleSeats(findRequestDTO);
		return ResponseEntity.ok(scheduleSeatDTOs);
	}
}
