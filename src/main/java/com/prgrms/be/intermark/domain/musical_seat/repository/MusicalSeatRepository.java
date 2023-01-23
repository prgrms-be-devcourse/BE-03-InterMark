package com.prgrms.be.intermark.domain.musical_seat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;

public interface MusicalSeatRepository extends JpaRepository<MusicalSeat, Long> {

    List<MusicalSeat> findAllByMusical(Musical musical);
}
