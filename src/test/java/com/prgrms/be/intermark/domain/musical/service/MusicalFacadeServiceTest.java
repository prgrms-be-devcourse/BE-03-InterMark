package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.actor.dto.ActorResponseDTO;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailImageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
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

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @DisplayName("뮤지컬 상세 조회 성공")
    void findMusicalDetailSuccess() {
        // given
        Musical musical = createMusical("title", ViewRating.ALL, Genre.COMEDY, "description", LocalDate.now(), LocalDate.now().plusDays(10L), 60,
                "thumbnailUrl", Stadium.builder().name("stadium").build(), User.builder().build());
        for (int i = 0; i < 2; i++) {
            Casting.builder()
                    .musical(musical)
                    .actor(Actor.builder().name("actor" + i).build())
                    .build();

            MusicalDetailImage.builder()
                    .musical(musical)
                    .imageUrl("imageUrl" + i)
                    .build();
        }
        when(musicalService.findMusicalById(any(Long.class)))
                .thenReturn(musical);

        MusicalDetailResponseDTO answer = MusicalDetailResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .rate(musical.getViewRating())
                .genre(musical.getGenre())
                .thumbnailUrl(musical.getThumbnailUrl())
                .description(musical.getDescription())
                .runningTime(musical.getRunningTime())
                .stadiumName(musical.getStadium().getName())
                .actors(ActorResponseDTO.listFromCastings(musical.getCastings()))
                .images(MusicalDetailImageResponseDTO.listFrom(musical.getDetailImages()))
                .build();

        // when
        MusicalDetailResponseDTO result = musicalFacadeService.findMusicalById(1L);

        // then
        assertThat(answer)
                .usingRecursiveComparison()
                .isEqualTo(result);
    }

    @Test
    @DisplayName("존재하지 않은 뮤지컬인 경우 뮤지컬 상세 조회 실패")
    void findNotExistsMusicalDetailFail() {
        // given
        when(musicalService.findMusicalById(any(Long.class)))
                .thenThrow(EntityNotFoundException.class);

        // when, then
        assertThrows(EntityNotFoundException.class, () -> musicalFacadeService.findMusicalById(1L));
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