package com.prgrms.be.intermark.domain.stadium.dto;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;

import lombok.Builder;

@Builder
public record StadiumFindResponseDTO(String name, String address, String imageUrl) {

	public static StadiumFindResponseDTO from(Stadium stadium) {
		return StadiumFindResponseDTO.builder()
			.name(stadium.getAddress())
			.address(stadium.getAddress())
			.imageUrl(stadium.getImageUrl())
			.build();
	}
}
