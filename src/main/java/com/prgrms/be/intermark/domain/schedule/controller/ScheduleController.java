package com.prgrms.be.intermark.domain.schedule.controller;

import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleCreateRequestDTO;
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
    public ResponseEntity<String> createSchedule(@RequestBody @Valid ScheduleCreateRequestDTO requestDto) {
        Long scheduleId = scheduleService.createSchedule(requestDto);

        return ResponseEntity
                .created(URI.create("/api/v1/schedules/" + scheduleId))
                .build();
    }
}
