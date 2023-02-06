package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;

public class TicketProvider {
    public static Ticket createTicket(User user, Schedule schedule, Seat seat, SeatGrade seatGrade, Musical musical, Stadium stadium) {
        return Ticket.builder()
                .ticketStatus(TicketStatus.AVAILABLE)
                .user(user)
                .schedule(schedule)
                .seat(seat)
                .seatGrade(seatGrade)
                .musical(musical)
                .stadium(stadium)
                .build();
    }
}
