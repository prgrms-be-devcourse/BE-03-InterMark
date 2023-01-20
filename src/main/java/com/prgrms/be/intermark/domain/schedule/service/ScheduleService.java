package com.prgrms.be.intermark.domain.schedule.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MusicalRepository musicalRepository;

    @Transactional
    public Long createSchedule(ScheduleCreateRequestDTO requestDto) {
        Musical musical = musicalRepository.findById(requestDto.musicalId())
                .orElseThrow(() -> new EntityNotFoundException("해당 뮤지컬이 존재하지 않습니다."));

        Schedule schedule = scheduleRepository.save(requestDto.toEntity(musical));

        return schedule.getId();
    }
}
