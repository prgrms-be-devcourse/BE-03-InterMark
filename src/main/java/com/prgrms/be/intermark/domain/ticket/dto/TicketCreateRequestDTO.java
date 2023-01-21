package com.prgrms.be.intermark.domain.ticket.dto;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.ticket.Ticket;
import com.prgrms.be.intermark.domain.ticket.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record TicketCreateRequestDTO(@NotNull Long userId,
                                     @NotNull Long seatId,
                                     @NotNull Long scheduleId,
                                     @Positive @NotNull int price) {

    public Ticket toEntity(User user, ScheduleSeat scheduleSeat) {
        return Ticket.builder()
                .user(user)
                .scheduleSeat(scheduleSeat)
                .price(price)
                .ticketStatus(TicketStatus.AVAILABLE)
                .build();
    }
}
