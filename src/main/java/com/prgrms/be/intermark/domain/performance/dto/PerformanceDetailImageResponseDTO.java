package com.prgrms.be.intermark.domain.performance.dto;

import com.prgrms.be.intermark.domain.performance.PerformanceDetailImage;

public record PerformanceDetailImageResponseDTO(String performanceDetailImageUrl) {

    public static PerformanceDetailImageResponseDTO from(PerformanceDetailImage performanceDetailImage) {
        return new PerformanceDetailImageResponseDTO(performanceDetailImage.getImageUrl());
    }
}
