package com.prgrms.be.intermark.domain.ticket.service;

import javax.persistence.EntityNotFoundException;
import com.prgrms.be.intermark.domain.ticket.dto.TicketFindResponseDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketFindResponseDTOs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.ticket.Ticket;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketDeleteResponseDto;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public TicketDeleteResponseDto deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("해당하는 예매가 없습니다.");
                });

        ticket.deleteTicket();

        return TicketDeleteResponseDto.builder()
                .ticketId(ticketId)
                .status(ticket.getTicketStatus())
                .message("티켓이 삭제 처리 되었습니다.")
                .build();
    }

    @Transactional(readOnly = true)
    public TicketFindResponseDTOs getAllTicket() {
        List<Ticket> tickets = ticketRepository.findAll();

        List<TicketFindResponseDTO> ticketFindResponseDTOs = new ArrayList<>();

        tickets.forEach((ticket) ->
                ticketFindResponseDTOs.add(TicketFindResponseDTO.from(ticket))
        );

        return TicketFindResponseDTOs.builder()
                .ticketFindResponseDTOs(ticketFindResponseDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public TicketFindResponseDTO getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 예매 내역이 존재하지 않습니다."));

        return TicketFindResponseDTO.from(ticket);
    }
}