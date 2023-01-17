package com.prgrms.be.intermark.domain.performance.dto;

import com.prgrms.be.intermark.domain.actor.dto.ActorInfoResponseDTO;
import com.prgrms.be.intermark.domain.actor.dto.ActorInfoResponseDTOs;
import com.prgrms.be.intermark.domain.casting.Casting;
import com.prgrms.be.intermark.domain.performance.Genre;
import com.prgrms.be.intermark.domain.performance.Performance;
import com.prgrms.be.intermark.domain.performance.PerformanceRating;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PerformanceDetailResponseDTO(
        String performanceName,
        LocalDate startDate,
        LocalDate endDate,
        PerformanceRating possibleAge,
        Genre genre,
        String thumbnailUrl,
        String description,
        int runningTime,
        int performancePrice,
        List<String> stadiumName,
        ActorInfoResponseDTOs actors,
        PerformanceDetailImageResponseDTOs images
) {

    public static PerformanceDetailResponseDTO from(Performance performance, List<Casting> castings) {

        List<String> stadiumNames = getStadiumNames(performance);

        ActorInfoResponseDTOs actors = getActorInfoResponseDTOs(castings);

        PerformanceDetailImageResponseDTOs images = new PerformanceDetailImageResponseDTOs(
                getPerformanceDetailImageResponseDTOs(performance)
        );

        return PerformanceDetailResponseDTO.builder()
                .performanceName(performance.getName())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .possibleAge(performance.getPossibleAge())
                .genre(performance.getGenre())
                .thumbnailUrl(performance.getThumbnailUrl())
                .description(performance.getDescription())
                .runningTime(performance.getRunningTime())
                .performancePrice(performance.getPrice())
                .stadiumName(stadiumNames)
                .actors(actors)
                .images(images)
                .build();
    }


    private static List<String> getStadiumNames(Performance performance) {
        return performance.getPerformanceStadiums()
                .stream()
                .map(performanceStadium -> performanceStadium.getStadium().getName())
                .toList();
    }

    private static ActorInfoResponseDTOs getActorInfoResponseDTOs(List<Casting> castings) {
        return new ActorInfoResponseDTOs(
                castings
                        .stream()
                        .map(casting -> ActorInfoResponseDTO.from(casting.getActor()))
                        .toList()
        );
    }

    private static List<PerformanceDetailImageResponseDTO> getPerformanceDetailImageResponseDTOs(Performance performance) {
        return performance.getPerformanceDetailImages()
                .stream()
                .map(PerformanceDetailImageResponseDTO::from)
                .toList();
    }
}
