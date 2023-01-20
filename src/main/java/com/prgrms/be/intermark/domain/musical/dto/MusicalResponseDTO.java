package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MusicalResponseDTO(String musicalTitle, String stadiumName, LocalDate startDate, LocalDate endDate) {

    public static MusicalResponseDTO from(Musical musical) {
        return MusicalResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .stadiumName(musical.getStadium().getName())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .build();
    }
}
