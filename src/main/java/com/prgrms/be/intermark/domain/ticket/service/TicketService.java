package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.ticket.Ticket;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    @Transactional
    public Long createTicket(TicketCreateRequestDTO ticketCreateRequestDTO) {

        User user = userRepository.findById(ticketCreateRequestDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        ScheduleSeat scheduleSeat = scheduleSeatRepository.findByScheduleIdAndSeatId(ticketCreateRequestDTO.seatId(), ticketCreateRequestDTO.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스케줄_좌석입니다."));

        if (scheduleSeat.isReserved()) {
            throw new IllegalArgumentException("이미 예약된 자리입니다.");
        }

        scheduleSeat.makeScheduleSeatIsReserved();

        Ticket ticket = ticketCreateRequestDTO.toEntity(user, scheduleSeat);

        ticketRepository.save(ticket);

        return ticket.getId();
    }
}
