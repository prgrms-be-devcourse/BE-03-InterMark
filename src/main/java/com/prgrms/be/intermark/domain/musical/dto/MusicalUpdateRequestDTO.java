package com.prgrms.be.intermark.domain.musical.dto;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Builder
public record MusicalUpdateRequestDTO(
        @NotBlank String title,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @NotBlank String description,
        @NotNull ViewRating viewRating,
        @NotNull Genre genre,
        @NotNull @Positive int runningTime,
        @NotNull Long managerId,
        @NotNull @Positive Long stadiumId,
        @NotNull List<Long> actors,
        @NotNull List<MusicalSeatGradeUpdateRequestDTO> seatGrades,
        @NotNull List<MusicalSeatUpdateRequestDTO> seats
) {

}
