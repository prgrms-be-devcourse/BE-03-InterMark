package com.prgrms.be.intermark.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.seat.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
