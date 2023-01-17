package com.prgrms.be.intermark.domain.performance.dto;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.prgrms.be.intermark.domain.performance.model.Genre;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.model.PerformanceRating;

import lombok.Builder;

@Builder
public record PerformanceCreateRequestDTO(
	@NotNull LocalDate startDate,
	@NotNull LocalDate endDate,
	@NotBlank String name,
	@NotNull int runningTime,
	@NotNull PerformanceRating possibleAge,
	@NotNull Genre genre,
	@NotNull String thumbnailUrl,
	@NotNull String description,
	@NotNull @Positive int price,
	@NotNull Long stadiumId,
	List<Long> actorIds,
	List<String> detailImageUrls
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
