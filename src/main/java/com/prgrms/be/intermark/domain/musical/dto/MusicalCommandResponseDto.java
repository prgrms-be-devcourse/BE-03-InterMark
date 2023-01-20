package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.Builder;

@Builder
public record MusicalCommandResponseDto(Long id) {

	public static MusicalCommandResponseDto from(Musical musical) {
		return MusicalCommandResponseDto.builder()
			.id(musical.getId())
			.build();
	}
}
