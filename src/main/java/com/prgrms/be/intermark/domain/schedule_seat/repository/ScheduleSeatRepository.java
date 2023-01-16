package com.prgrms.be.intermark.domain.schedule_seat.repository;

import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    Optional<ScheduleSeat> findByScheduleAndSeat(Schedule schedule, Seat seat);
}
