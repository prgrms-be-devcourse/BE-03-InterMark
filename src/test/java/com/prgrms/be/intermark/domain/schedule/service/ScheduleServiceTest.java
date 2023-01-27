package com.prgrms.be.intermark.domain.schedule.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.dto.ScheduleCreateRequestDTO;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Success - 새로운 스케줄을 등록하면 해당 스케줄 저장 - createSchedule")
    @Transactional
    void createSchedule() {
        // given
        Stadium stadium = Stadium.builder()
                .name("stadium")
                .address("Korea Seoul")
                .imageUrl("image")
                .build();

        User user = User.builder()
                .social(Social.GOOGLE)
                .socialId("1234")
                .refreshToken("refreshToken")
                .nickname("유저")
                .role(UserRole.ADMIN)
                .isDeleted(false)
                .birth(LocalDate.now())
                .build();

        Musical musical = Musical.builder()
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

}