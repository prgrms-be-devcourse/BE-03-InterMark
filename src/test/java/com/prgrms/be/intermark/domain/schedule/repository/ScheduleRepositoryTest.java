package com.prgrms.be.intermark.domain.schedule.repository;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private StadiumRepository stadiumRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MusicalRepository musicalRepository;

    private final Stadium stadium = Stadium.builder()
            .name("stadium")
            .address("Korea Seoul")
            .imageUrl("image")
            .build();

    User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1")
            .role(UserRole.ROLE_USER)
            .nickname("유저")
            .email("example1@gmail.com")
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
    @DisplayName("Success - 중복되는 스케줄을 등록하면 겹치는 스케줄 수 반환")
    @Transactional
    void getSchedulesNumByStartTimeSuccess() {
        // given
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        scheduleRepository.save(schedule);

        // when
        int scheduleNum = scheduleRepository.getSchedulesNumByStartTime(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                musical.getStadium());

        // then
        assertThat(scheduleNum).isEqualTo(1);
    }

    @Test
    @DisplayName("Success - 중복되지 않는 스케줄을 등록하면 0 반환")
    @Transactional
    void getSchedulesNumZeroByStartTimeSuccess() {
        // given
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(musical.getRunningTime() + 100);
        Schedule schedule = Schedule.builder()
                .startTime(startTime)
                .endTime(startTime.plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        scheduleRepository.save(schedule);

        // when
        int scheduleNum = scheduleRepository.getSchedulesNumByStartTime(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                musical.getStadium());

        // then
        assertThat(scheduleNum).isEqualTo(0);
    }

    @Test
    @DisplayName("Success - 중복되는 시간으로 수정하면 중복되는 스케줄 수 반환")
    @Transactional
    void getDuplicatedScheduleExceptByIdSuccess() {
        // given
        Schedule schedule1 = Schedule.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusMinutes(musical.getRunningTime() + 100);
        Schedule schedule2 = Schedule.builder()
                .startTime(startTime)
                .endTime(startTime.plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // when
        int scheduleNum = scheduleRepository.getDuplicatedScheduleExceptById(
                schedule2.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(musical.getRunningTime()),
                stadium
        );

        // then
        assertThat(scheduleNum).isEqualTo(1);
    }

    @Test
    @DisplayName("Success - 중복되지 않는 시간으로 수정하면 0 반환")
    @Transactional
    void getDuplicatedScheduleZeroExceptByIdSuccess() {
        // given
        Schedule schedule1 = Schedule.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusMinutes(musical.getRunningTime() + 100);
        Schedule schedule2 = Schedule.builder()
                .startTime(startTime)
                .endTime(startTime.plusMinutes(musical.getRunningTime()))
                .musical(musical)
                .build();

        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // when
        startTime = startTime.plusMinutes(100);
        int scheduleNum = scheduleRepository.getDuplicatedScheduleExceptById(
                schedule2.getId(),
                startTime,
                startTime.plusMinutes(musical.getRunningTime()),
                stadium
        );

        // then
        assertThat(scheduleNum).isEqualTo(0);
    }

}