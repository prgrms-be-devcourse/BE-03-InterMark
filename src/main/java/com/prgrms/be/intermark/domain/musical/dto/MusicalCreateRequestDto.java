package com.prgrms.be.intermark.domain.musical.dto;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;

import lombok.Builder;

@Builder
public record MusicalCreateRequestDto(
	@NotBlank String title,
	@NotNull ViewRating viewRating,
	@NotNull Genre genre,
	@NotBlank String description,
	@NotNull LocalDate startDate,
	@NotNull LocalDate endDate,
	@NotNull @Positive int runningTime,
	@NotNull long managerId,
	@NotNull long stadiumId,
	List<Long> actors,
	List<MusicalSeatGradeCreateRequestDto> seatGrades,
	List<MusicalSeatCreateRequestDto> seats
) {

	public Musical toEntity() {
		return Musical.builder()
			.title(title)
			.viewRating(viewRating)
			.genre(genre)
			.description(description)
			.startDate(startDate)
			.endDate(endDate)
			.runningTime(runningTime)
			.build();
	}
}
