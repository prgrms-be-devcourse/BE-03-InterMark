package com.prgrms.be.intermark.domain.schedule.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleFindResponseDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTO;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTOs;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;

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
			.orElseThrow(() -> new EntityNotFoundException("?????? ???????????? ???????????? ????????????."));

        int duplicatedSchedulesNum = scheduleRepository.getSchedulesNumByStartTime(
                requestDto.getStartTime(),
                requestDto.getEndTime(musical),
                musical.getStadium());
		if (duplicatedSchedulesNum > 0) {
			throw new IllegalStateException("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????.");
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

	@Transactional
	public void updateSchedule(Long scheduleId, ScheduleUpdateRequestDTO requestDto) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new EntityNotFoundException("?????? ???????????? ???????????? ????????????."));

		if (schedule.isDeleted()) {
			throw new EntityNotFoundException("?????? ???????????? ???????????? ????????????.");
		}

		LocalDateTime startTime = requestDto.getStartTime();
		LocalDateTime endTime = requestDto.getEndTime(schedule.getMusical());

        int duplicatedSchedulesNum = scheduleRepository.getDuplicatedScheduleExceptById(
                scheduleId,
                startTime,
                endTime,
                schedule.getMusical().getStadium());
		if (duplicatedSchedulesNum > 0) {
			throw new IllegalStateException("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????.");
		}

		schedule.setScheduleTime(startTime, endTime);
	}

	@Transactional
	public void deleteSchedule(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new EntityNotFoundException("?????? ???????????? ???????????? ????????????."));

		List<Ticket> tickets = schedule.getTickets().stream().filter((Ticket::isReserved)).toList();

		if (tickets.size() > 0) {
			throw new IllegalStateException("????????? ???????????? ????????? ??? ????????????.");
		}

		if (schedule.isDeleted()) {
			throw new EntityNotFoundException("?????? ????????? ??????????????????.");
		}

		schedule.deleteSchedule();
	}

    @Transactional(readOnly = true)
    public ScheduleSeatResponseDTOs findScheduleSeats(Long scheduleId) {
        List<ScheduleSeatResponseDTO> scheduleSeats
                = scheduleSeatRepository.findAllByScheduleId(scheduleId)
                .stream()
                .map(ScheduleSeatResponseDTO::from)
                .toList();

        return ScheduleSeatResponseDTOs.builder()
                .scheduleSeats(scheduleSeats)
                .build();
    }

    @Transactional(readOnly = true)
    public ScheduleFindResponseDTO findSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("???????????? ?????? ??????????????????.");
                });

        return ScheduleFindResponseDTO.from(schedule);
    }

	@Transactional(readOnly = true)
	public PageResponseDTO<Schedule, ScheduleFindResponseDTO> findSchedulesByMusical(Long musicalId, Pageable pageable) {
		Musical musical = musicalRepository.findById(musicalId)
			.orElseThrow(() -> {
				throw new EntityNotFoundException("???????????? ?????? ??????????????????.");
			});

		Page<Schedule> schedulePage = scheduleRepository.findAllByMusical(musical, pageable);

		return new PageResponseDTO<>(
			schedulePage,
			ScheduleFindResponseDTO::from,
			PageListIndexSize.SCHEDULE_LIST_INDEX_SIZE
		);
	}

	@Transactional
	public void deleteAllByMusical(Musical musical) {
		scheduleRepository.findByMusicalAndIsDeletedIsFalse(musical)
			.forEach(Schedule::deleteSchedule);

	}

    public boolean existsByMusical(Musical musical) {
        return scheduleRepository.existsByMusicalAndIsDeletedFalse(musical);
    }
}