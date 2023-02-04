package com.prgrms.be.intermark.domain.schedule.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleFindResponseDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Object> createSchedule(@RequestBody @Valid ScheduleCreateRequestDTO requestDto) {
        Long scheduleId = scheduleService.createSchedule(requestDto);

        return ResponseEntity
                .created(URI.create("/api/v1/schedules/" + scheduleId))
                .build();
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Object> updateSchedule(@PathVariable("scheduleId") long scheduleId,
            @RequestBody @Valid ScheduleUpdateRequestDTO requestDto) {
        scheduleService.updateSchedule(scheduleId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Object> deleteSchedule(@PathVariable("scheduleId") long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<ScheduleSeatResponseDTOs> getScheduleSeats(@PathVariable Long scheduleId) {
        ScheduleSeatResponseDTOs scheduleSeats = scheduleService.findScheduleSeats(scheduleId);
        return ResponseEntity.ok(scheduleSeats);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleFindResponseDTO> getSchedule(@PathVariable Long scheduleId) {
        ScheduleFindResponseDTO schedule = scheduleService.findSchedule(scheduleId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<Schedule, ScheduleFindResponseDTO>> getScheduleByMusical(
        @RequestParam Long musical,
        Pageable pageable
    ) {
        PageResponseDTO<Schedule, ScheduleFindResponseDTO> schedules = scheduleService.findSchedulesByMusical(
            musical, pageable);

        return ResponseEntity.ok(schedules);
    }
}
