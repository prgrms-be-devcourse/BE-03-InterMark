package com.prgrms.be.intermark.domain.schedule.service;

import com.prgrms.be.intermark.domain.performance.Performance;
import com.prgrms.be.intermark.domain.performance.repository.PerformanceRepository;
import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.performance_stadium.repository.PerformanceStadiumRepository;
import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleRequestDTO;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.stadium.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PerformanceRepository performanceRepository;
    private final StadiumRepository stadiumRepository;
    private final PerformanceStadiumRepository performanceStadiumRepository;

    @Transactional
    public Long createSchedule(ScheduleRequestDTO scheduleRequestDto) {
        System.out.println(scheduleRequestDto.performanceId());
        Performance performance = performanceRepository.findById(scheduleRequestDto.performanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 공연이 존재하지 않습니다."));

        Stadium stadium = stadiumRepository.findById(scheduleRequestDto.stadiumId())
                .orElseThrow(() -> new EntityNotFoundException("해당 공연장이 존재하지 않습니다."));

        PerformanceStadium performanceStadium = performanceStadiumRepository.save(
                PerformanceStadium.builder()
                        .performance(performance)
                        .stadium(stadium)
                        .build());

        Schedule schedule = scheduleRepository.save(
                scheduleRequestDto.toEntity(performanceStadium)
        );

        return schedule.getId();
    }
}
