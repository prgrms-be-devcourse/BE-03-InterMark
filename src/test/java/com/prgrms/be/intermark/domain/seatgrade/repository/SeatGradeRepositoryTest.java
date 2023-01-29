package com.prgrms.be.intermark.domain.seatgrade.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.SeatGradeProvider;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeatGradeRepositoryTest {

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final SeatGrade seatGrade = SeatGradeProvider.createSeatGrade(musical);

    @BeforeEach
    void setUp() {
        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("성공 - 정상 좌석 등급 값이 들어오면 저장에 성공한다")
        void saveSuccess() {
            // given & when
            SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

            // then
            assertThat(savedSeatGrade.getId()).isNotNull();
            assertThat(savedSeatGrade)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(seatGrade);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("실패 - 좌석 등급의 이름으로 null, 빈 값, 공백이 들어오면 저장에 실패한다")
        void saveFailByWrongName(String wrongName) {
            // given
            SeatGrade wrongSeatGrade = SeatGrade.builder()
                    .name(wrongName)
                    .price(10000)
                    .musical(musical)
                    .build();

            // when & then
            assertThatThrownBy(() -> seatGradeRepository.save(wrongSeatGrade))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, 0})
        @DisplayName("실패 - 좌석 등급의 가격으로 음수, 0 이 들어오면 저장에 실패한다")
        void saveFailByWrongPrice(int wrongPrice) {
            // given
            SeatGrade wrongSeatGrade = SeatGrade.builder()
                    .name("VIP")
                    .price(wrongPrice)
                    .musical(musical)
                    .build();

            // when & then
            assertThatThrownBy(() -> seatGradeRepository.save(wrongSeatGrade))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("실패 - 연관된 뮤지컬 값이 없으면 저장에 실패한다")
        void saveFailByNoMusical() {
            // given
            SeatGrade wrongSeatGrade = SeatGrade.builder()
                    .name("VIP")
                    .price(10000)
                    .build();

            // when & then
            assertThatThrownBy(() -> seatGradeRepository.save(wrongSeatGrade))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }

}