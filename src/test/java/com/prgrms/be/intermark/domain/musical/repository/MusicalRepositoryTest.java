package com.prgrms.be.intermark.domain.musical.repository;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MusicalRepositoryTest {

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        stadiumRepository.save(stadium);
        userRepository.save(user);
    }

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Success - 올바른 값이 들어오면 뮤지컬을 저장한다")
        void saveSuccess() {
            // given & when
            Musical savedMusical = musicalRepository.save(musical);
            Musical findMusical = musicalRepository.findById(savedMusical.getId()).get();

            // then
            assertThat(findMusical).isEqualTo(savedMusical);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Fail - 뮤지컬 제목 값에 null, 빈 값, 공백이 입력되면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Fail - 뮤지컬 썸네일 URL 값에 null, 빈 값, 공백이 입력되면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 관람등급 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 장르 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 설명 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 시작일 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 종료일 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 상영시간 값이 입력되지 않으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, 0})
        @DisplayName("Fail - 뮤지컬 상영시간 값에 음수나 0 이 입력되면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 Stadium 값이 없으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 뮤지컬 관리 User 값이 없으면 저장에 실패한다")
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

            // when & then
            assertThatThrownBy(() -> musicalRepository.save(musical))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

    }
}