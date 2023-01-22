package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    @Transactional
    public Long createTicket(TicketCreateRequestDTO ticketCreateRequestDTO) {
        User user = userRepository.findByIdAndIsDeletedIsFalse(ticketCreateRequestDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 유저입니다."));

        ScheduleSeat scheduleSeat = scheduleSeatRepository.findByScheduleSeatFetch(ticketCreateRequestDTO.scheduleSeatId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 스케줄좌석입니다."));

        if (scheduleSeat.isReserved()) {
            throw new IllegalArgumentException("이미 예약된 좌석입니다.");
        }

        if (scheduleSeat.getSchedule().isOver(LocalDateTime.now())) {
            throw new IllegalArgumentException("이미 지난 스케줄입니다.");
        }

        Ticket ticket = ticketCreateRequestDTO.toEntity(user, scheduleSeat);
        ticketRepository.save(ticket);
        scheduleSeat.reserve();

        return ticket.getId();
    }
}
