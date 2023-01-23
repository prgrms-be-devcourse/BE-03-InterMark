package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.dto.*;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<Ticket, TicketResponseDTO> getAllTickets(Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);
        return new PageResponseDTO<>(ticketPage, TicketResponseDTO::from, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);
    }
}
