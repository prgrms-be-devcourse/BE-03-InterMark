package com.prgrms.be.intermark.domain.schedule.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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

        int duplicatedSchedulesNum = scheduleRepository.getSchedulesNumByStartTime(
                requestDto.getStartTime(),
                requestDto.getEndTime(musical),
                musical.getStadium());
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

	@Transactional
	public void updateSchedule(Long scheduleId, ScheduleUpdateRequestDTO requestDto) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new EntityNotFoundException("해당 스케줄이 존재하지 않습니다."));

		if (schedule.isDeleted()) {
			throw new EntityNotFoundException("해당 스케줄이 존재하지 않습니다.");
		}

		LocalDateTime startTime = requestDto.getStartTime();
		LocalDateTime endTime = requestDto.getEndTime(schedule.getMusical());

        int duplicatedSchedulesNum = scheduleRepository.getDuplicatedScheduleExceptById(
                scheduleId,
                startTime,
                endTime,
                schedule.getMusical().getStadium());
		if (duplicatedSchedulesNum > 0) {
			throw new IllegalStateException("해당 시작 시간에 이미 다른 스케줄이 존재합니다.");
		}

		schedule.setScheduleTime(startTime, endTime);
	}

	@Transactional
	public void deleteSchedule(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new EntityNotFoundException("해당 스케줄이 존재하지 않습니다."));

		List<Ticket> tickets = schedule.getTickets().stream().filter((Ticket::isReserved)).toList();

		if (tickets.size() > 0) {
			throw new IllegalStateException("예매된 스케줄은 삭제할 수 없습니다.");
		}

		if (schedule.isDeleted()) {
			throw new EntityNotFoundException("이미 삭제된 스케줄입니다.");
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
                    throw new EntityNotFoundException("존재하지 않는 스케줄입니다.");
                });

        return ScheduleFindResponseDTO.from(schedule);
    }

	@Transactional(readOnly = true)
	public PageResponseDTO<Schedule, ScheduleFindResponseDTO> findSchedulesByMusical(Long musicalId, Pageable pageable) {
		Musical musical = musicalRepository.findById(musicalId)
			.orElseThrow(() -> {
				throw new EntityNotFoundException("존재하지 않는 뮤지컬입니다.");
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