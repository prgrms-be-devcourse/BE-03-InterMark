package com.prgrms.be.intermark.domain.schedule_seat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatFindRequestDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleSeatService {

	private final ScheduleSeatRepository scheduleSeatRepository;

	@Transactional(readOnly = true)
	public ScheduleSeatResponseDTOs getScheduleSeats(ScheduleSeatFindRequestDTO findRequestDTO) {

		List<ScheduleSeat> reservedScheduleSeats = scheduleSeatRepository.findScheduleSeatsByScheduleId(findRequestDTO.scheduleId());

		List<ScheduleSeatResponseDTO> scheduleSeatResponseDTOs = reservedScheduleSeats.stream()
			.map(ScheduleSeatResponseDTO::from)
			.toList();

		return new ScheduleSeatResponseDTOs(scheduleSeatResponseDTOs);
	}
}
