package com.prgrms.be.intermark.domain.performance.dto;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.be.intermark.domain.performance.model.Genre;
import com.prgrms.be.intermark.domain.performance.model.PerformanceRating;

import lombok.Builder;

@Builder
public record PerformanceUpdateRequestDTO(
	LocalDate startDate,
	LocalDate endDate,
	String name,
	int runningTime,
	PerformanceRating possibleAge,
	Genre genre,
	String thumbnailUrl,
	String description,
	int price,
	List<String> detailImageUrls,
	String stadium,
	List<String> actors
) {

}
