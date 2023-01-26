package com.prgrms.be.intermark.domain.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT COUNT(s) FROM Schedule s " +
            "WHERE s.isDeleted = false AND s.startTime <= :endTime AND s.endTime >= :startTime " +
            "AND s.musical.stadium = :stadium")
    int getSchedulesNumByStartTime(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("stadium") Stadium stadium
    );

    @Query("SELECT COUNT(s) FROM Schedule s " +
            "WHERE NOT s.id = :scheduleId AND s.startTime <= :endTime AND s.endTime >= :startTime " +
            "AND s.isDeleted = false AND s.musical.stadium = :stadium")
    int getDuplicatedScheduleExceptById(
            @Param("scheduleId") Long scheduleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("stadium") Stadium stadium
    );

    boolean existsByMusicalAndIsDeletedFalse(Musical musical);

    Page<Schedule> findAllByMusical(Musical musical, Pageable pageable);

    List<Schedule> findAllByMusicalAndIsDeletedIsFalse(Musical musical);
}
