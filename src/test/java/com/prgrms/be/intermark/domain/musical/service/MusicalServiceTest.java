package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicalServiceTest {

    @InjectMocks
    private MusicalService musicalService;

    @Mock
    private MusicalRepository musicalRepository;

    @Nested
    @DisplayName("save")
    class Save {

        String thumbnailUrl = "abcedf";
        Stadium stadium = Stadium.builder()
                .name("예술의 전당")
                .address("서울특별시")
                .imageUrl("abcefg")
                .build();
        User user = User.builder()
                .birth(LocalDate.of(1997, 10, 10))
                .social(Social.GOOGLE)
                .socialId("intermark")
                .refreshToken("abcdefg")
                .nickname("인터마크 관리자")
                .role(UserRole.ADMIN)
                .isDeleted(false)
                .email("intermark@gmail.com")
                .build();

        @Test
        @DisplayName("성공 - 올바른 값이 들어오면 뮤지컬을 저장한다")
        void saveSuccess() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenReturn(any(Musical.class));

            // when
            musicalService.save(musical);

            // then
            verify(musicalRepository).save(musical);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("실패 - 뮤지컬 제목 값에 null, 빈 값, 공백이 입력되면 저장에 실패한다")
        void saveFailByWrongTitle(String wrongTitle) {
            // given
            Musical musical = Musical.builder()
                    .title(wrongTitle)
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("실패 - 뮤지컬 썸네일 URL 값에 null, 빈 값, 공백이 입력되면 저장에 실패한다")
        void saveFailByWrongThumbnailUrl(String wrongUrl) {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(wrongUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 관람등급 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoViewRating() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 장르 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoGenre() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 설명 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoDescription() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 시작일 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoStartDate() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 종료일 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoEndDate() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 상영시간 값이 입력되지 않으면 저장에 실패한다")
        void saveFailByNoRunningTime() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, 0})
        @DisplayName("실패 - 뮤지컬 상영시간 값에 음수나 0 이 입력되면 저장에 실패한다")
        void saveFailByWrongRunningTime(int wrongRunningTime) {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(wrongRunningTime)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }


        @Test
        @DisplayName("실패 - 뮤지컬 Stadium 값이 없으면 저장에 실패한다")
        void saveFailByNoStadium() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .user(user)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

        @Test
        @DisplayName("실패 - 뮤지컬 관리 User 값이 없으면 저장에 실패한다")
        void saveFailByNoUser() {
            // given
            Musical musical = Musical.builder()
                    .title("제목")
                    .viewRating(ViewRating.ADULT)
                    .genre(Genre.DRAMA)
                    .description("설명")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 2, 1))
                    .runningTime(60)
                    .thumbnailUrl(thumbnailUrl)
                    .stadium(stadium)
                    .build();
            when(musicalRepository.save(musical)).thenThrow(ConstraintViolationException.class);

            // when & then
            Assertions.assertThatThrownBy(() -> musicalService.save(musical))
                    .isInstanceOf(ConstraintViolationException.class);
            verify(musicalRepository).save(musical);
        }

    }
}