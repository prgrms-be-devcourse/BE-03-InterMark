package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByMusicalDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByUserDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final MusicalRepository musicalRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final PageService pageService;

    @Transactional
    public Long createTicket(TicketCreateRequestDTO ticketCreateRequestDTO) {
        User user = userRepository.findByIdAndIsDeletedFalse(ticketCreateRequestDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ???????????????."));

        ScheduleSeat scheduleSeat = scheduleSeatRepository.findByScheduleSeatFetchWithLock(ticketCreateRequestDTO.scheduleSeatId())
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ????????????????????????."));

        if (scheduleSeat.isReserved()) {
            throw new IllegalArgumentException("?????? ????????? ???????????????.");
        }

        if (scheduleSeat.getSchedule().isOver(LocalDateTime.now())) {
            throw new IllegalArgumentException("?????? ?????? ??????????????????.");
        }

        Ticket ticket = ticketCreateRequestDTO.toEntity(user, scheduleSeat);
        ticketRepository.save(ticket);
        scheduleSeat.reserve();

        return ticket.getId();
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseDTO> getAllTickets(Pageable pageable) {
        PageRequest pageRequest = pageService.getPageRequest(pageable, (int) ticketRepository.count());
        Page<Ticket> ticketPage = ticketRepository.findAll(pageRequest);
        return new PageResponseDTO<>(ticketPage, TicketResponseDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseByUserDTO> getTicketsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("?????? ????????? ???????????? ????????????."));

        if (user.isDeleted()) {
            throw new EntityNotFoundException("?????? ????????? ???????????? ????????????.");
        }

        PageRequest pageRequest = pageService.getPageRequest(pageable, (int) ticketRepository.countByUser(user));
        Page<Ticket> ticketPage = ticketRepository.findByUser(user, pageRequest);

        return new PageResponseDTO<>(
                ticketPage, TicketResponseByUserDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseByMusicalDTO> getTicketsByMusical(Long musicalId, Pageable pageable) {
        Musical musical = musicalRepository.findById(musicalId)
                .orElseThrow(() -> new EntityNotFoundException("?????? ???????????? ???????????? ????????????."));

        if (musical.isDeleted()) {
            throw new EntityNotFoundException("?????? ???????????? ???????????? ????????????.");
        }

        PageRequest pageRequest = pageService.getPageRequest(pageable, (int) ticketRepository.countByMusical(musical));
        Page<Ticket> ticketPage = ticketRepository.findByMusical(musical, pageRequest);

        return new PageResponseDTO<>(
                ticketPage, TicketResponseByMusicalDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("?????? ????????? ???????????? ????????????."));
        return TicketResponseDTO.from(ticket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("???????????? ?????? ???????????????.");
                });

        if (ticket.isDeleted()) {
            throw new EntityNotFoundException("?????? ????????? ???????????????.");
        }

        Schedule schedule = ticket.getSchedule();
        Seat seat = ticket.getSeat();
        Optional<ScheduleSeat> scheduleSeat = scheduleSeatRepository.findByScheduleAndSeat(schedule, seat);
        scheduleSeat.ifPresent(ScheduleSeat::refund);

        ticket.deleteTicket();
    }

    public boolean existsByMusical(Musical musical) {
        return ticketRepository.existsByMusical(musical);
    }
}
