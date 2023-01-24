package com.prgrms.be.intermark.domain.ticket.dto;

import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

@Builder
public record TicketResponseMusicalDTO(
        @NotNull String title,
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startTime,
        @NotNull int runningTime,
        @NotNull ViewRating rating) {
}
