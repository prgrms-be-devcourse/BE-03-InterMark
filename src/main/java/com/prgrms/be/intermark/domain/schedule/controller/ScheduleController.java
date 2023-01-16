package com.prgrms.be.intermark.domain.schedule.controller;

import com.prgrms.be.intermark.domain.schedule.dto.ScheduleRequestDTO;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Long> createSchedule(@RequestBody @Valid ScheduleRequestDTO scheduleRequestDto) {
        Long scheduleId = scheduleService.createSchedule(scheduleRequestDto);

        return ResponseEntity
                .created(URI.create("/schedules/" + scheduleId))
                .body(scheduleId);
    }
}
