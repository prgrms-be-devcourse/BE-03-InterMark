package com.prgrms.be.intermark.domain.ticket.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByMusical(Musical musical);

    Page<Ticket> findByUser(User user, Pageable pageable);

    Page<Ticket> findByMusical(Musical musical, Pageable pageable);

    long countByUser(User user);

    long countByMusical(Musical musical);
}
