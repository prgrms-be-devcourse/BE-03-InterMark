package com.prgrms.be.intermark.domain.musical.dto;

import java.time.LocalDate;

import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.Builder;

@Builder
public record MusicalSummaryResponseDTO(String musicalTitle, String stadiumName, LocalDate startDate, LocalDate endDate) {

    public static MusicalSummaryResponseDTO from(Musical musical) {
        return MusicalSummaryResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .stadiumName(musical.getStadium().getName())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .build();
    }
}
