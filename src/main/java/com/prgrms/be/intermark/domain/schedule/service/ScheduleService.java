package com.prgrms.be.intermark.domain.schedule.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dtos.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
