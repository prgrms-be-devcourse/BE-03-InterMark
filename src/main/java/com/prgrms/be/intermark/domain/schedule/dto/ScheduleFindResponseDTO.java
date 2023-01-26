package com.prgrms.be.intermark.domain.schedule.dto;

import java.time.LocalDateTime;

import com.prgrms.be.intermark.domain.schedule.model.Schedule;

import lombok.Builder;

@Builder
public record ScheduleFindResponseDTO(
	boolean isDeleted,
	String musicalName,
	String stadiumName,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public static ScheduleFindResponseDTO from(Schedule schedule) {
		return ScheduleFindResponseDTO.builder()
			.isDeleted(schedule.isDeleted())
			.musicalName(schedule.getMusical().getTitle())
			.stadiumName(schedule.getMusical().getStadium().getName())
			.startTime(schedule.getStartTime())
			.endTime(schedule.getEndTime())
			.build();
	}
}
