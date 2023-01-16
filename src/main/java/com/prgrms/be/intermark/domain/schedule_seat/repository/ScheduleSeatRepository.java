package com.prgrms.be.intermark.domain.schedule_seat.repository;

import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    @Query("select s from ScheduleSeat s where s.seat.id = :seatId and s.schedule.id = :scheduleId")
    Optional<ScheduleSeat> findByScheduleIdAndSeatId(@Param("seatId") Long seatId, @Param("scheduleId") Long scheduleId);
}
