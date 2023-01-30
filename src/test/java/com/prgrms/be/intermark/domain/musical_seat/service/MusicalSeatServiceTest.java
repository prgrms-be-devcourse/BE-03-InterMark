package com.prgrms.be.intermark.domain.musical_seat.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicalSeatServiceTest {

    @InjectMocks
    private MusicalSeatService musicalSeatService;

    @Mock
    private MusicalSeatRepository musicalSeatRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final SeatGrade seatGrade = SeatGradeProvider.createSeatGrade(musical);

    @Test
    @DisplayName("성공 - 정상적인 뮤지컬좌석 값이 들어오면 저장에 성공한다 - save")
    void saveSuccess() {
        // given
        Seat seat1 = SeatProvider.createSeat("A", 1, stadium);
        Seat seat2 = SeatProvider.createSeat("A", 2, stadium);
        MusicalSeat musicalSeat1 = MusicalSeatProvider.createMusicalSeat(musical, seat1, seatGrade);
        MusicalSeat musicalSeat2 = MusicalSeatProvider.createMusicalSeat(musical, seat2, seatGrade);

        List<MusicalSeat> musicalSeats = List.of(musicalSeat1, musicalSeat2);
        when(musicalSeatRepository.save(any(MusicalSeat.class))).thenReturn(any(MusicalSeat.class));

        // when
        musicalSeatService.save(musicalSeats);

        // then
        verify(musicalSeatRepository, times(musicalSeats.size())).save(any(MusicalSeat.class));
    }

    @Test
    @DisplayName("성공 - 해당 뮤지컬의 뮤지컬 좌석을 전부 삭제한다.")
    void deleteAllByMusicalSuccess() {
        // given
        List<MusicalSeat> musicalSeats = List.of(mock(MusicalSeat.class), mock(MusicalSeat.class));
        when(musicalSeatRepository.findByMusicalAndIsDeletedIsFalse(musical)).thenReturn(musicalSeats);

        // when
        musicalSeatService.deleteAllByMusical(musical);

        // then
        verify(musicalSeatRepository).findByMusicalAndIsDeletedIsFalse(musical);
        for (MusicalSeat musicalSeat : musicalSeats) {
            verify(musicalSeat).deleteMusicalSeat();
        }
    }
}