package com.prgrms.be.intermark.domain.ticket.dto;

import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;

import lombok.Builder;

@Builder
public record TicketResponseDTO(
        @NotNull Long ticketId,
        @NotNull String nickname,
        @NotNull TicketResponseMusicalDTO musical,
        @NotNull TicketResponseSeatDTO seat,
        @NotNull TicketResponseStadiumDTO stadium,
        @NotNull TicketStatus ticketStatus
) {

    public static TicketResponseDTO from(Ticket ticket) {
        Musical musical = ticket.getMusical();
        Schedule schedule = ticket.getSchedule();
        Seat seat = ticket.getSeat();
        SeatGrade seatGrade = ticket.getSeatGrade();
        Stadium stadium = ticket.getStadium();
        User user = ticket.getUser();

        return TicketResponseDTO.builder()
                .ticketId(ticket.getId())
                .nickname(user.getNickname())
                .musical(TicketResponseMusicalDTO.builder()
                        .title(musical.getTitle())
                        .startTime(schedule.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .runningTime(musical.getRunningTime())
                        .rating(musical.getViewRating())
                        .build())
                .seat(TicketResponseSeatDTO.builder()
                        .grade(seatGrade.getName())
                        .seatNum(seat.getRowNum() + seat.getColumnNum())
                        .build())
                .stadium(TicketResponseStadiumDTO.builder()
                        .name(stadium.getName())
                        .address(stadium.getAddress())
                        .build())
                .ticketStatus(ticket.getTicketStatus())
                .build();
    }
}
