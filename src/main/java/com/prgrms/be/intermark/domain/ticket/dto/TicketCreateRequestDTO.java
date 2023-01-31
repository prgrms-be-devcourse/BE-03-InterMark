package com.prgrms.be.intermark.domain.ticket.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;

import lombok.Builder;

@Builder
public record TicketCreateRequestDTO(
        @NotNull @Positive Long userId,
        @NotNull @Positive Long scheduleSeatId
) {
    public Ticket toEntity(User user, ScheduleSeat scheduleSeat) {
        return Ticket.builder()
                .user(user)
                .seat(scheduleSeat.getSeat())
                .schedule(scheduleSeat.getSchedule())
                .seatGrade(scheduleSeat.getSeatGrade())
                .musical(scheduleSeat.getSeatGrade().getMusical())
                .stadium(scheduleSeat.getSeat().getStadium())
                .ticketStatus(TicketStatus.AVAILABLE)
                .build();
    }
}
