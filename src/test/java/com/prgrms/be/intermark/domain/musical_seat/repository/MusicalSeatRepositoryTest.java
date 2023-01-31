package com.prgrms.be.intermark.domain.musical_seat.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MusicalSeatRepositoryTest {

    @Autowired
    private MusicalSeatRepository musicalSeatRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final Seat seat = SeatProvider.createSeat(stadium);
    private final SeatGrade seatGrade = SeatGradeProvider.createSeatGrade(musical);
    private final MusicalSeat musicalSeat = MusicalSeatProvider.createMusicalSeat(musical, seat, seatGrade);

    @BeforeEach
    void setUp() {
        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        seatRepository.save(seat);
        seatGradeRepository.save(seatGrade);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Success - 정상적인 뮤지컬좌석 값이 입력되면 저장에 성공한다")
        void saveSuccess() {
            // given & when
            MusicalSeat savedMusicalSeat = musicalSeatRepository.save(musicalSeat);

            // then
            assertThat(savedMusicalSeat.getId()).isNotNull();
            assertThat(savedMusicalSeat)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(musicalSeat);
        }


        @Test
        @DisplayName("Fail - 연관된 뮤지컬 값이 없으면 저장에 실패한다")
        void saveFailByNoMusical() {
            // given
            MusicalSeat musicalSeat = MusicalSeatProvider.createMusicalSeat(null, seat, seatGrade);

            // when & then
            assertThatThrownBy(() -> musicalSeatRepository.save(musicalSeat))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 연관된 좌석 값이 없으면 저장에 실패한다")
        void saveFailByNoSeat() {
            // given
            MusicalSeat musicalSeat = MusicalSeatProvider.createMusicalSeat(musical, null, seatGrade);

            // when & then
            assertThatThrownBy(() -> musicalSeatRepository.save(musicalSeat))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 연관된 좌석 등급 값이 없으면 저장에 실패한다")
        void saveFailByNoSeatGrade() {
            // given
            MusicalSeat musicalSeat = MusicalSeatProvider.createMusicalSeat(musical, seat, null);

            // when & then
            assertThatThrownBy(() -> musicalSeatRepository.save(musicalSeat))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }
    }

}