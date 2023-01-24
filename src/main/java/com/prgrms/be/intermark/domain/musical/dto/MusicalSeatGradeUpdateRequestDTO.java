package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record MusicalSeatGradeUpdateRequestDTO(@NotBlank String seatGradeName, @NotNull @Positive int seatGradePrice) {

    public SeatGrade toEntity() {
        return SeatGrade.builder()
                .name(seatGradeName)
                .price(seatGradePrice)
                .build();
    }
}
