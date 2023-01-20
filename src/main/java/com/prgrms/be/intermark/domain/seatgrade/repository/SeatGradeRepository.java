package com.prgrms.be.intermark.domain.seatgrade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

	Optional<SeatGrade> findByName(String name);
}
