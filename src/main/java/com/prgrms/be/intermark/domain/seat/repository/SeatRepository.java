package com.prgrms.be.intermark.domain.seat.repository;

import com.prgrms.be.intermark.domain.seat.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.id = :seatId AND s.stadium.id = :stadiumId")
    Optional<Seat> findByIdAndStadium(@Param("seatId") Long seatId, @Param("stadiumId") Long stadiumId);
}
