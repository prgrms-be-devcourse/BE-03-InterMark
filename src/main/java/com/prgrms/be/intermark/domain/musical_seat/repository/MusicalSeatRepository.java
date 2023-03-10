package com.prgrms.be.intermark.domain.musical_seat.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicalSeatRepository extends JpaRepository<MusicalSeat, Long> {

    List<MusicalSeat> findAllByMusical(Musical musical);

    void deleteByMusical(Musical musical);

    List<MusicalSeat> findByMusicalAndIsDeletedIsFalse(Musical musical);

}
