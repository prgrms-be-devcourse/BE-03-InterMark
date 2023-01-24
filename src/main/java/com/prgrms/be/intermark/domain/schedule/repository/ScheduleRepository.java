package com.prgrms.be.intermark.domain.schedule.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.startTime <= :endTime AND s.endTime >= :startTime")
    int getSchedulesNumByStartTime(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(s) FROM Schedule s " +
            "WHERE NOT s.id = :scheduleId AND s.startTime <= :endTime AND s.endTime >= :startTime")
    int getDuplicatedScheduleExceptById(
            @Param("scheduleId") Long scheduleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    boolean existsByMusicalAndIsDeletedFalse(Musical musical);

    Page<Schedule> findAllByMusical(Musical musical, Pageable pageable);
}
