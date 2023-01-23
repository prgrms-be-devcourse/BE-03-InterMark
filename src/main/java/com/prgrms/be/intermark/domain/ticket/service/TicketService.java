package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.ticket.dto.*;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final MusicalRepository musicalRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseDTO> getAllTickets(Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);
        return new PageResponseDTO<>(ticketPage, TicketResponseDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseByUserDTO> getTicketsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        if (user.isDeleted()) {
            throw new EntityNotFoundException("해당 유저가 존재하지 않습니다.");
        }

        Page<Ticket> ticketPage = ticketRepository.findByUser(user, pageable);
        return new PageResponseDTO<>(
                ticketPage, TicketResponseByUserDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);

    }

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseByMusicalDTO> getTicketsByMusical(Long musicalId, Pageable pageable) {
        Musical musical = musicalRepository.findById(musicalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 뮤지컬이 존재하지 않습니다."));

        if (musical.isDeleted()) {
            throw new EntityNotFoundException("해당 뮤지컬이 존재하지 않습니다.");
        }

        Page<Ticket> ticketPage = ticketRepository.findByMusical(musical, pageable);
        return new PageResponseDTO<>(
                ticketPage, TicketResponseByMusicalDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("해당 티켓이 존재하지 않습니다."));
        return TicketResponseDTO.from(ticket);
    }
}
