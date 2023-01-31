package com.prgrms.be.intermark.domain.schedule.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private MusicalRepository musicalRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MusicalSeatRepository musicalSeatRepository;

    private final Stadium stadium = Stadium.builder()
            .name("stadium")
            .address("Korea Seoul")
            .imageUrl("image")
            .build();

    private final User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1234")
            .nickname("유저")
            .role(UserRole.ROLE_ADMIN)
            .build();

    private final Musical musical = Musical.builder()
            .title("title")
            .thumbnailUrl("thumbnail")
            .viewRating(ViewRating.ALL)
            .genre(Genre.COMEDY)
            .description("description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .runningTime(80)
            .stadium(stadium)
            .user(user)
            .build();

    @Test
    @DisplayName("Success - 새로운 스케줄을 등록하면 해당 스케줄 저장")
    void createScheduleSuccess() {
        // given
        ScheduleCreateRequestDTO scheduleCreateRequestDTO = ScheduleCreateRequestDTO.builder()
                .musicalId(1L)
                .startTime("2000-01-01 11:00")
                .build();

        Schedule schedule = scheduleCreateRequestDTO.toEntity(musical);

        when(musicalRepository.findById(any(Long.class))).thenReturn(Optional.of(musical));
        when(scheduleRepository.getSchedulesNumByStartTime(
                scheduleCreateRequestDTO.getStartTime(),
                scheduleCreateRequestDTO.getEndTime(musical),
                stadium
        )).thenReturn(0);
        when(scheduleRepository.save(any())).thenReturn(schedule);
        when(musicalSeatRepository.findAllByMusical(any())).thenReturn(new ArrayList<>());

        // when
        Long savedScheduleId = scheduleService.createSchedule(scheduleCreateRequestDTO);

        // then
        verify(musicalRepository).findById(any(Long.class));
        verify(scheduleRepository).getSchedulesNumByStartTime(scheduleCreateRequestDTO.getStartTime(),
                scheduleCreateRequestDTO.getEndTime(musical),
                stadium);
        verify(scheduleRepository).save(any());
        verify(musicalSeatRepository).findAllByMusical(any());

        assertThat(savedScheduleId).isEqualTo(schedule.getId());
    }

    @Test
    @DisplayName("Fail - 해당 뮤지컬이 없으면 EntityNotFoundException 발생")
    void notExistedMusicalFail() {
        // given
        ScheduleCreateRequestDTO scheduleCreateRequestDTO = ScheduleCreateRequestDTO.builder()
                .musicalId(any(Long.class))
                .startTime("2000-01-01 11:00")
                .build();

        // when - then
        assertThatThrownBy(() -> scheduleService.createSchedule(scheduleCreateRequestDTO))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 뮤지컬이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("Fail - 겹치는 스케줄이 있으면 IllegalStateException 발생")
    void duplicatedScheduleFail() {
        // given
        ScheduleCreateRequestDTO scheduleCreateRequestDTO = ScheduleCreateRequestDTO.builder()
                .musicalId(1L)
                .startTime("2000-01-01 11:00")
                .build();

        when(musicalRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(musical));
        when(scheduleRepository.getSchedulesNumByStartTime(
                scheduleCreateRequestDTO.getStartTime(),
                scheduleCreateRequestDTO.getEndTime(musical),
                stadium
        )).thenReturn(1);

        // when - then
        assertThatThrownBy(() -> scheduleService.createSchedule(scheduleCreateRequestDTO))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("해당 시작 시간에 이미 다른 스케줄이 존재합니다.");

        verify(musicalRepository).findById(any(Long.class));
        verify(scheduleRepository).getSchedulesNumByStartTime(scheduleCreateRequestDTO.getStartTime(),
                scheduleCreateRequestDTO.getEndTime(musical),
                stadium);
    }

    @Test
    @DisplayName("Success - 새로운 시작시간을 입력하면 스케줄 수정")
    void updateScheduleSuccess() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        ScheduleUpdateRequestDTO scheduleUpdateRequestDTO = ScheduleUpdateRequestDTO.builder()
                .startTime("2022-12-31 21:00").build();

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(scheduleRepository.getDuplicatedScheduleExceptById(
                schedule.getId(),
                scheduleUpdateRequestDTO.getStartTime(),
                scheduleUpdateRequestDTO.getEndTime(schedule.getMusical()),
                schedule.getMusical().getStadium()
        )).thenReturn(0);

        // when
        scheduleService.updateSchedule(schedule.getId(), scheduleUpdateRequestDTO);

        // then
        verify(scheduleRepository).findById(schedule.getId());
        verify(scheduleRepository).getDuplicatedScheduleExceptById(
                schedule.getId(),
                scheduleUpdateRequestDTO.getStartTime(),
                scheduleUpdateRequestDTO.getEndTime(schedule.getMusical()),
                schedule.getMusical().getStadium()
        );

        assertThat(schedule.getStartTime()).isEqualTo(scheduleUpdateRequestDTO.getStartTime());
    }

    @Test
    @DisplayName("Fail - 해당 스케줄이 존재하지 않으면 EntityNotFoundException 발생")
    void notExistedScheduleOnUpdateFail() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        ScheduleUpdateRequestDTO scheduleUpdateRequestDTO = ScheduleUpdateRequestDTO.builder()
                .startTime("2022-12-31 21:00").build();

        // when - then
        assertThatThrownBy(() -> scheduleService.updateSchedule(schedule.getId(), scheduleUpdateRequestDTO))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 스케줄이 존재하지 않습니다.");
    }


    @Test
    @DisplayName("Fail - 겹치는 스케줄이 있으면 IllegalStateException 발생")
    void duplicatedNewScheduleFail() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        ScheduleUpdateRequestDTO scheduleUpdateRequestDTO = ScheduleUpdateRequestDTO.builder()
                .startTime("2022-12-31 21:00").build();

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(scheduleRepository.getDuplicatedScheduleExceptById(
                schedule.getId(),
                scheduleUpdateRequestDTO.getStartTime(),
                scheduleUpdateRequestDTO.getEndTime(schedule.getMusical()),
                schedule.getMusical().getStadium()
        )).thenReturn(1);

        // when - then
        assertThatThrownBy(() -> scheduleService.updateSchedule(schedule.getId(), scheduleUpdateRequestDTO))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("해당 시작 시간에 이미 다른 스케줄이 존재합니다.");

        verify(scheduleRepository).findById(schedule.getId());
        verify(scheduleRepository).getDuplicatedScheduleExceptById(
                schedule.getId(),
                scheduleUpdateRequestDTO.getStartTime(),
                scheduleUpdateRequestDTO.getEndTime(schedule.getMusical()),
                schedule.getMusical().getStadium()
        );
    }

    @Test
    @DisplayName("Success - 스케줄을 삭제하면 schedule.isDeleted 값 변경")
    void deleteScheduleSuccess() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));

        // when
        scheduleService.deleteSchedule(schedule.getId());

        // then
        verify(scheduleRepository).findById(schedule.getId());
        assertThat(schedule.isDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("Fail - 스케줄이 존재하지 않으면 EntityNotFoundException 발생")
    void notExistedScheduleOnDeleteFail() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        // when - then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(schedule.getId()))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 스케줄이 존재하지 않습니다.");
    }

}