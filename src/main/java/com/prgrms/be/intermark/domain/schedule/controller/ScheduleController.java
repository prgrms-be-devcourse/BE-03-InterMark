package com.prgrms.be.intermark.domain.schedule.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@RestController
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
}
