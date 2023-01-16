package com.prgrms.be.intermark.domain.seat.repository;

import com.prgrms.be.intermark.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
