package com.prgrms.be.intermark.domain.musical.dto;

import java.util.List;

import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;

import lombok.Builder;

@Builder
public record MusicalDetailImageResponseDTO(String musicalDetailImageUrl) {

	public static MusicalDetailImageResponseDTO from(MusicalDetailImage musicalDetailImage) {
		return MusicalDetailImageResponseDTO.builder()
			.musicalDetailImageUrl(musicalDetailImage.getImageUrl())
			.build();
	}

	public static List<MusicalDetailImageResponseDTO> listFrom(List<MusicalDetailImage> musicalDetailImages) {
		return musicalDetailImages.stream()
			.map(MusicalDetailImageResponseDTO::from)
			.toList();
	}

}