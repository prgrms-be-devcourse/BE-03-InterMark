package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.Builder;

@Builder
public record MusicalCommandResponseDTO(Long id) {

	public static MusicalCommandResponseDTO from(Musical musical) {
		return MusicalCommandResponseDTO.builder()
			.id(musical.getId())
			.build();
	}
}
