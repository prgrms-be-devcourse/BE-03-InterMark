package com.prgrms.be.intermark.domain.schedule.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MusicalRepository musicalRepository;
    private final MusicalSeatRepository musicalSeatRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    @Transactional
    public Long createSchedule(ScheduleCreateRequestDTO requestDto) {
        Musical musical = musicalRepository.findById(requestDto.musicalId())
                .orElseThrow(() -> new EntityNotFoundException("해당 뮤지컬이 존재하지 않습니다."));

        int duplicatedSchedulesNum = scheduleRepository.getSchedulesNumByStartTime(requestDto.getStartTime(),
                requestDto.getEndTime(musical));
        if (duplicatedSchedulesNum > 0) {
            throw new IllegalStateException("해당 시작 시간에 이미 다른 스케줄이 존재합니다.");
        }

        Schedule schedule = scheduleRepository.save(requestDto.toEntity(musical));

        List<MusicalSeat> musicalSeats = musicalSeatRepository.findAllByMusical(musical);
        musicalSeats.forEach((musicalSeat -> {
            ScheduleSeat scheduleSeat = ScheduleSeat.builder()
                    .isReserved(false)
                    .schedule(schedule)
                    .seat(musicalSeat.getSeat())
                    .seatGrade(musicalSeat.getSeatGrade())
                    .build();
            scheduleSeatRepository.save(scheduleSeat);
        }));

        return schedule.getId();
    }
}
