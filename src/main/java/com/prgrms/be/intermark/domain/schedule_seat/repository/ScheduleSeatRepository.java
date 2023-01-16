package com.prgrms.be.intermark.domain.schedule_seat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.seat.Seat;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    Optional<ScheduleSeat> findByScheduleAndSeat(Schedule schedule, Seat seat);

    @Query("select s from ScheduleSeat s where s.schedule.id = :scheduleId ")
    List<ScheduleSeat> findScheduleSeatsByScheduleId(@Param("scheduleId") Long scheduleId);
}
