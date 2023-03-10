package com.prgrms.be.intermark.domain.schedule.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleFindResponseDTO;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleUpdateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.dto.ScheduleSeatResponseDTO;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;

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

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;

    private final Stadium stadium = Stadium.builder()
            .name("stadium")
            .address("Korea Seoul")
            .imageUrl("image")
            .build();

    private final User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1234")
            .nickname("??????")
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
    @DisplayName("Success - ????????? ???????????? ???????????? ?????? ????????? ??????")
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
    @DisplayName("Fail - ?????? ???????????? ????????? EntityNotFoundException ??????")
    void notExistedMusicalFail() {
        // given
        ScheduleCreateRequestDTO scheduleCreateRequestDTO = ScheduleCreateRequestDTO.builder()
                .musicalId(any(Long.class))
                .startTime("2000-01-01 11:00")
                .build();

        // when - then
        assertThatThrownBy(() -> scheduleService.createSchedule(scheduleCreateRequestDTO))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("?????? ???????????? ???????????? ????????????.");
    }

    @Test
    @DisplayName("Fail - ????????? ???????????? ????????? IllegalStateException ??????")
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
                .hasMessage("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????.");

        verify(musicalRepository).findById(any(Long.class));
        verify(scheduleRepository).getSchedulesNumByStartTime(scheduleCreateRequestDTO.getStartTime(),
                scheduleCreateRequestDTO.getEndTime(musical),
                stadium);
    }

    @Test
    @DisplayName("Success - ????????? ??????????????? ???????????? ????????? ??????")
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
    @DisplayName("Fail - ?????? ???????????? ???????????? ????????? EntityNotFoundException ??????")
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
                .hasMessage("?????? ???????????? ???????????? ????????????.");
    }


    @Test
    @DisplayName("Fail - ????????? ???????????? ????????? IllegalStateException ??????")
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
                .hasMessage("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????.");

        verify(scheduleRepository).findById(schedule.getId());
        verify(scheduleRepository).getDuplicatedScheduleExceptById(
                schedule.getId(),
                scheduleUpdateRequestDTO.getStartTime(),
                scheduleUpdateRequestDTO.getEndTime(schedule.getMusical()),
                schedule.getMusical().getStadium()
        );
    }

    @Test
    @DisplayName("Success - ???????????? ???????????? schedule.isDeleted ??? ??????")
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
    @DisplayName("Fail - ?????? ????????? ?????? ???????????? ???????????? IllegalStateException ??????")
    void deleteScheduleHasTicketFail() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.parse("2022-12-31 11:00", formatter))
                .endTime(LocalDateTime.parse("2022-12-31 12:20", formatter))
                .musical(musical)
                .build();

        Ticket ticket = Ticket.builder()
                .ticketStatus(TicketStatus.AVAILABLE)
                .user(user)
                .schedule(schedule)
                .seat(Seat.builder().build())
                .seatGrade(SeatGrade.builder().build())
                .musical(musical)
                .stadium(stadium)
                .build();

        ticket.setSchedule(schedule);

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));

        // when - then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(schedule.getId()))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("????????? ???????????? ????????? ??? ????????????.");

        verify(scheduleRepository).findById(schedule.getId());
    }

    @Test
    @DisplayName("Fail - ???????????? ???????????? ????????? EntityNotFoundException ??????")
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
                .hasMessage("?????? ???????????? ???????????? ????????????.");
    }

    @Test
    @DisplayName("Success - ?????? ???????????? ???????????? ?????? ????????????.")
    void deleteAllByMusicalSuccess() {
        // given
        List<Schedule> schedules = List.of(mock(Schedule.class), mock(Schedule.class));
        when(scheduleRepository.findByMusicalAndIsDeletedIsFalse(musical)).thenReturn(schedules);

        // when
        scheduleService.deleteAllByMusical(musical);

        // then
        verify(scheduleRepository).findByMusicalAndIsDeletedIsFalse(musical);
        for (Schedule schedule : schedules) {
            verify(schedule).deleteSchedule();
        }
    }

    @Test
    @DisplayName("Success - ?????? ???????????? ?????? ????????? ?????? ????????????. - findScheduleSeats")
    void findScheduleSeatsSuccess() {
        // Given
        Long scheduleId = 1L;
        ScheduleSeat scheduleSeat1 = mock(ScheduleSeat.class);
        ScheduleSeat scheduleSeat2 = mock(ScheduleSeat.class);
        List<ScheduleSeat> scheduleSeats = List.of(scheduleSeat1, scheduleSeat2);

        try (MockedStatic<ScheduleSeatResponseDTO> scheduleSeatResponseDTO = mockStatic(ScheduleSeatResponseDTO.class)) {
            when(scheduleSeatRepository.findAllByScheduleId(scheduleId)).thenReturn(scheduleSeats);
            scheduleSeatResponseDTO.when(() -> ScheduleSeatResponseDTO.from(any(ScheduleSeat.class)))
                    .thenReturn(any(ScheduleSeatResponseDTO.class));

            // when
            scheduleService.findScheduleSeats(scheduleId);

            // then
            verify(scheduleSeatRepository).findAllByScheduleId(scheduleId);
            scheduleSeatResponseDTO.verify(() -> ScheduleSeatResponseDTO.from(any(ScheduleSeat.class)), times(scheduleSeats.size()));
        }
    }

    @Nested
    @DisplayName("findSchedule")
    class FindSchedule {

        @Test
        @DisplayName("Success - ????????? ?????? ????????? ????????????.")
        void findScheduleSuccess() {
            // given
            Long scheduleId = 1L;
            Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(2023, 3, 15, 18, 30))
                .endTime(LocalDateTime.of(2023, 3, 15, 20, 30))
                .musical(musical)
                .build();
            when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

            // when
            ScheduleFindResponseDTO scheduleInfo = scheduleService.findSchedule(scheduleId);

            // then
            verify(scheduleRepository).findById(scheduleId);
            assertThat(scheduleInfo).hasFieldOrPropertyWithValue("isDeleted", schedule.isDeleted())
                .hasFieldOrPropertyWithValue("musicalName", schedule.getMusical().getTitle())
                .hasFieldOrPropertyWithValue("stadiumName", schedule.getMusical().getStadium().getName())
                .hasFieldOrPropertyWithValue("startTime", schedule.getStartTime())
                .hasFieldOrPropertyWithValue("endTime", schedule.getEndTime());
        }

        @Test
        @DisplayName("Fail - ???????????? ???????????? ????????? ????????? ????????????.")
        void findScheduleFail() {
            // given
            Long scheduleId = 1L;
            when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.findSchedule(scheduleId))
                .isExactlyInstanceOf(EntityNotFoundException.class);
            verify(scheduleRepository).findById(scheduleId);
        }
    }
}