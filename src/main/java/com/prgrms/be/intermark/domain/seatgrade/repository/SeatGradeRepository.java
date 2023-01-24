package com.prgrms.be.intermark.domain.seatgrade.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

	Optional<SeatGrade> findByNameAndMusical(String name, Musical musical);

	void deleteByMusical(Musical musical);
}
