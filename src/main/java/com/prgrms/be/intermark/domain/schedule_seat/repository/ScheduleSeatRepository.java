package com.prgrms.be.intermark.domain.schedule_seat.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.seat.model.Seat;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ScheduleSeat ss LEFT JOIN FETCH ss.schedule sd LEFT JOIN FETCH ss.seat s LEFT JOIN FETCH ss.seatGrade sg LEFT JOIN FETCH sd.musical m LEFT JOIN FETCH m.stadium st WHERE ss.id = :scheduleSeatId")
    Optional<ScheduleSeat> findByScheduleSeatFetchWithLock(@Param("scheduleSeatId") Long scheduleSeatId);

    @Query("SELECT s FROM ScheduleSeat s LEFT JOIN FETCH s.schedule LEFT JOIN FETCH s.seat WHERE s.schedule.id = :scheduleId ")
    List<ScheduleSeat> findAllByScheduleId(@Param("scheduleId") Long scheduleId);

    Optional<ScheduleSeat> findByScheduleAndSeat(Schedule schedule, Seat seat);
}
