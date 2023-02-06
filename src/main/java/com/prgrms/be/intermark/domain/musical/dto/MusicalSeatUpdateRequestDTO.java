package com.prgrms.be.intermark.domain.musical.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Builder;

@Builder
public record MusicalSeatUpdateRequestDTO(@NotNull @Positive Long seatId, @NotBlank String seatGradeName) {
}
