package com.prgrms.be.intermark.domain.schedule.repository;

import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
