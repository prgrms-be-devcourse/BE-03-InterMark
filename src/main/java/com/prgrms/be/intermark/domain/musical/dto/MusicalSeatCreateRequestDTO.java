package com.prgrms.be.intermark.domain.musical.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record MusicalSeatCreateRequestDTO(
	@NotNull Long seatId,
	@NotBlank String seatGradeName) {
}
