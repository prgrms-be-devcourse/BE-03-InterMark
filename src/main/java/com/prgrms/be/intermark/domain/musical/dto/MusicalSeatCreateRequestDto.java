package com.prgrms.be.intermark.domain.musical.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record MusicalSeatCreateRequestDto(
	@NotBlank Long seatId,
	@NotBlank String seatGradeName) {
}
