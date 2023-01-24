package com.prgrms.be.intermark.domain.schedule_seat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    @Query("SELECT ss FROM ScheduleSeat ss LEFT JOIN FETCH ss.schedule sd LEFT JOIN FETCH ss.seat s LEFT JOIN FETCH ss.seatGrade sg LEFT JOIN FETCH sd.musical m LEFT JOIN FETCH m.stadium st WHERE ss.id = :scheduleSeatId")
    Optional<ScheduleSeat> findByScheduleSeatFetch(@Param("scheduleSeatId") Long scheduleSeatId);

    @Query("SELECT s FROM ScheduleSeat s WHERE s.schedule.id = :scheduleId ")
    List<ScheduleSeat> findAllByScheduleId(@Param("scheduleId") Long scheduleId);

}
