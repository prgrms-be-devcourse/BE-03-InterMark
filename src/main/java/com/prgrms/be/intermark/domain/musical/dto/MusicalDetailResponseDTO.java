package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.actor.dto.ActorResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical_detail_image.dto.MusicalDetailImageResponseDTO;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MusicalDetailResponseDTO(String musicalTitle, LocalDate startDate, LocalDate endDate, ViewRating rate,
                                       Genre genre, String thumbnailUrl, String description, int runningTime,
                                       String stadiumName, List<ActorResponseDTO> actors,
                                       List<MusicalDetailImageResponseDTO> images) {
    public static MusicalDetailResponseDTO from(Musical musical) {
        return MusicalDetailResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .rate(musical.getViewRating())
                .genre(musical.getGenre())
                .thumbnailUrl(musical.getThumbnailUrl())
                .description(musical.getDescription())
                .runningTime(musical.getRunningTime())
                .stadiumName(musical.getStadium().getName())
                .actors(ActorResponseDTO.listFromCastings(musical.getCastings()))
                .images(MusicalDetailImageResponseDTO.listFrom(musical.getMusicalDetailImages()))
                .build();
    }
}
