package com.prgrms.be.intermark.domain.performance.dto;

import com.prgrms.be.intermark.domain.performance.Performance;

import java.time.LocalDate;
import java.util.List;

public record PerformanceInfoResponseDTO(String performanceName, List<String> stadiumNames, LocalDate startDate,
                                         LocalDate endDate) {

    public static PerformanceInfoResponseDTO from(Performance performance) {

        List<String> stadiumNames = performance.getPerformanceStadiums()
                .stream()
                .map(performanceStadium -> performanceStadium.getPerformance().getName())
                .toList();

        return new PerformanceInfoResponseDTO(performance.getName(), stadiumNames, performance.getStartDate(), performance.getEndDate());
    }
}
