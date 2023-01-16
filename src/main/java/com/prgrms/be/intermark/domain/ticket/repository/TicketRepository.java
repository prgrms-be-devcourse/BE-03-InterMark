package com.prgrms.be.intermark.domain.ticket.repository;

import com.prgrms.be.intermark.domain.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
