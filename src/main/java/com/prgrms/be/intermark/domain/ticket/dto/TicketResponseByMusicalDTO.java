package com.prgrms.be.intermark.domain.ticket.dto;

import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;

@Builder
public record TicketResponseByMusicalDTO(
        @NotNull Long ticketId,
        @NotNull String nickname,
        @NotNull TicketResponseSeatDTO seat,
        @NotNull TicketResponseStadiumDTO stadium,
        @NotNull TicketStatus ticketStatus
) {

    public static TicketResponseByMusicalDTO from(Ticket ticket) {
        Schedule schedule = ticket.getSchedule();
        Seat seat = ticket.getSeat();
        SeatGrade seatGrade = ticket.getSeatGrade();
        Stadium stadium = ticket.getStadium();
        User user = ticket.getUser();

        return TicketResponseByMusicalDTO.builder()
                .ticketId(ticket.getId())
                .nickname(user.getNickname())
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

