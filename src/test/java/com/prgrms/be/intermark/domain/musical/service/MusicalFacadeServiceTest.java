package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicalFacadeServiceTest {

    @InjectMocks
    private MusicalFacadeService musicalFacadeService;

    @Mock
    private MusicalService musicalService;

    @Test
    @DisplayName("뮤지컬 리스트 조회 성공")
    void findAllMusicalSuccess() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<Musical> musicals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            musicals.add(createMusical("title" + i, ViewRating.ALL, Genre.COMEDY, "description" + i, LocalDate.now(), LocalDate.now().plusDays(10L), i,
                    "thumbnailUrl" + i, Stadium.builder().name("stadium" + i).build(), User.builder().build()));
        }
        Page<Musical> musicalPage = new PageImpl<>(musicals);
        when(musicalService.findAllMusicals(pageable))
                .thenReturn(musicalPage);
        List<MusicalSummaryResponseDTO> answer = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            answer.add(MusicalSummaryResponseDTO.builder()
                    .musicalTitle(musicals.get(i).getTitle())
                    .stadiumName(musicals.get(i).getStadium().getName())
                    .startDate(musicals.get(i).getStartDate())
                    .endDate(musicals.get(i).getEndDate())
                    .build());
        }

        // when
        PageResponseDTO<Musical, MusicalSummaryResponseDTO> result = musicalFacadeService.findAllMusicals(pageable);

        // then
        for (int i = 0; i < 10; i++) {
            assertThat(result.getData().get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(answer.get(i));
        }
    }

    private Musical createMusical(String title, ViewRating viewRating, Genre genre, String description, LocalDate startDate, LocalDate endDate, int runningTime,
                                  String thumbnailUrl, Stadium stadium, User user) {
        return Musical.builder()
                .title(title)
                .viewRating(viewRating)
                .genre(genre)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .runningTime(runningTime)
                .thumbnailUrl(thumbnailUrl)
                .stadium(stadium)
                .user(user)
                .build();
    }
}