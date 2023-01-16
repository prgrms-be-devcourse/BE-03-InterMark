package com.prgrms.be.intermark.domain.ticket.repository;

import com.prgrms.be.intermark.domain.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
