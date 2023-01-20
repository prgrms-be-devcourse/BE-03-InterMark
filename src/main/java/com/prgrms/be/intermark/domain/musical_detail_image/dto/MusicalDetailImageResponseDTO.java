package com.prgrms.be.intermark.domain.musical_detail_image.dto;

import com.prgrms.be.intermark.domain.musical_detail_image.model.MusicalDetailImage;
import lombok.Builder;

import java.util.List;

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
