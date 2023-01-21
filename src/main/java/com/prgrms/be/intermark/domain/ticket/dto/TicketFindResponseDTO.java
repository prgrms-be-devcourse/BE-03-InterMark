package com.prgrms.be.intermark.domain.ticket.dto;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.seat.Seat;
import com.prgrms.be.intermark.domain.ticket.Ticket;
import com.prgrms.be.intermark.domain.ticket.TicketStatus;
import com.prgrms.be.intermark.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TicketFindResponseDTO(
        String performance,
        String stadium,
        int price,
        TicketStatus ticketStatus,
        long userId,
        String username,
        String rowNum,
        int columnNum,
        LocalDateTime scheduleTime
) {
    public static TicketFindResponseDTO from(Ticket ticket) {
        User user = ticket.getUser();
        Schedule schedule = ticket.getScheduleSeat().getSchedule();
        PerformanceStadium performanceStadium = schedule.getPerformanceStadium();
        Seat seat = ticket.getScheduleSeat().getSeat();

        return TicketFindResponseDTO.builder()
                .performance(performanceStadium.getPerformance().getName())
                .stadium(performanceStadium.getStadium().getName())
                .price(ticket.getPrice())
                .ticketStatus(ticket.getTicketStatus())
                .userId(user.getId())
                .username(user.getUsername())
                .rowNum(seat.getRowNum())
                .columnNum(seat.getColumnNum())
                .scheduleTime(schedule.getStartTime())
                .build();
    }
}
