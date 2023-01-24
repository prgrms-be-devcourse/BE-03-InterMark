package com.prgrms.be.intermark.domain.schedule_seat.dto;

import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;

import lombok.Builder;

@Builder
public record ScheduleSeatResponseDTO(
	boolean isReserved,
	Long scheduleId,
	Long seatId,
	String seatNum
) {

	public static ScheduleSeatResponseDTO from(ScheduleSeat scheduleSeat) {
		return ScheduleSeatResponseDTO.builder()
			.isReserved(scheduleSeat.isReserved())
			.scheduleId(scheduleSeat.getSchedule().getId())
			.seatId(scheduleSeat.getSeat().getId())
			.seatNum(scheduleSeat.getSeat().getRowNum() + scheduleSeat.getSeat().getColumnNum())
			.build();
	}
}
