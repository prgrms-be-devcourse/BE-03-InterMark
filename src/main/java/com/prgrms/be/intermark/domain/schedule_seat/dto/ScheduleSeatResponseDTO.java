package com.prgrms.be.intermark.domain.schedule_seat.dto;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;

import lombok.Builder;

@Builder
public record ScheduleSeatResponseDTO(
	boolean isReserved,
	Long scheduleId,
	Long seatId,
	String rowNum,
	int columnNum
) {
	public static ScheduleSeatResponseDTO from(ScheduleSeat scheduleSeat) {
		return ScheduleSeatResponseDTO.builder()
			.isReserved(scheduleSeat.isReserved())
			.scheduleId(scheduleSeat.getSchedule().getId())
			.seatId(scheduleSeat.getSeat().getId())
			.rowNum(scheduleSeat.getSeat().getRowNum())
			.columnNum(scheduleSeat.getSeat().getColumnNum())
			.build();
	}
}
