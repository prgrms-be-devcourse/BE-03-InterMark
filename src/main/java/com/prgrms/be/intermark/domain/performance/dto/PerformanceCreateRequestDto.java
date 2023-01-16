package com.prgrms.be.intermark.domain.performance.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.prgrms.be.intermark.domain.performance.model.Genre;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.model.PerformanceRating;

import lombok.Builder;

@Builder
public record PerformanceCreateRequestDto(
	@NotNull LocalDate startDate,
	@NotNull LocalDate endDate,
	@NotNull String name,
	@NotNull int runningTime,
	@NotNull PerformanceRating possibleAge,
	@NotNull Genre genre,
	@NotNull String thumbnailUrl,
	@NotNull String description,
	@NotNull int price
) {
	public Performance toEntity() {
		return Performance.builder()
			.startDate(startDate)
			.endDate(endDate)
			.name(name)
			.runningTime(runningTime)
			.possibleAge(possibleAge)
			.genre(genre)
			.thumbnailUrl(thumbnailUrl)
			.description(description)
			.price(price)
			.build();
	}
}
