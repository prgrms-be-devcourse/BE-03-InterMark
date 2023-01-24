package com.prgrms.be.intermark.domain.musical.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record MusicalSeatUpdateRequestDTO(@NotNull @Positive Long seatId, @NotBlank String seatGradeName) {
}
