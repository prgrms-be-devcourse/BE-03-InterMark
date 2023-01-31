package com.prgrms.be.intermark.domain.seatgrade.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.SeatGradeProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
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
class SeatGradeServiceTest {

    @InjectMocks
    private SeatGradeService seatGradeService;

    @Mock
    private SeatGradeRepository seatGradeRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);

    @Test
    @DisplayName("Success - 정상적인 좌석 등급 값이 들어오면 저장에 성공한다 - save")
    void saveSuccess() {
        // given
        SeatGrade seatGrade1 = SeatGradeProvider.createSeatGrade("VIP", 10000, musical);
        SeatGrade seatGrade2 = SeatGradeProvider.createSeatGrade("R", 5000, musical);
        List<SeatGrade> seatGrades = List.of(seatGrade1, seatGrade2);
        when(seatGradeRepository.save(any(SeatGrade.class))).thenReturn(any(SeatGrade.class));

        // when
        seatGradeService.save(seatGrades);

        // then
        verify(seatGradeRepository, times(seatGrades.size())).save(any(SeatGrade.class));
    }

    @Test
    @DisplayName("Success - 해당 뮤지컬의 좌석등급을 전부 삭제한다. - deleteAllByMusical")
    void deleteAllByMusicalSuccess() {
        // given
        List<SeatGrade> seatGrades = List.of(mock(SeatGrade.class), mock(SeatGrade.class));
        when(seatGradeRepository.findByMusicalAndIsDeletedIsFalse(musical)).thenReturn(seatGrades);

        // when
        seatGradeService.deleteAllByMusical(musical);

        // then
        verify(seatGradeRepository).findByMusicalAndIsDeletedIsFalse(musical);
        for (SeatGrade seatGrade : seatGrades) {
            verify(seatGrade).deleteSeatGrade();
        }
    }
}