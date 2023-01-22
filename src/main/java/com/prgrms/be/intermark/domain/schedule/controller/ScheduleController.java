package com.prgrms.be.intermark.domain.schedule.controller;

import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

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
