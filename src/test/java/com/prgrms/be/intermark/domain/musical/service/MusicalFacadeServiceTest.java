package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.actor.dto.ActorResponseDTO;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailImageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalDetailImageRepository;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MusicalFacadeServiceTest {

    @Autowired
    private MusicalFacadeService musicalFacadeService;

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private CastingRepository castingRepository;

    @Autowired
    private MusicalDetailImageRepository musicalDetailImageRepository;

    @Test
    @DisplayName("Success - 뮤지컬 리스트 조회 시 뮤지컬 정보 리스트로 반환 - findAllMusicals")
    void getAllMusicalsSuccess() {
        // given
        User user = TestUtil.createUser(Social.GOOGLE, "socialId", "refreshToken", "nickname", UserRole.ADMIN, false, LocalDate.now(), "email@naver.com");
        Stadium stadium = TestUtil.createStadium("name", "address", "imageUrl");
        userRepository.save(user);
        stadiumRepository.save(stadium);

        List<MusicalSummaryResponseDTO> musicals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Musical musical = TestUtil.createMusical("title" + i, "description" + i, LocalDate.now(), LocalDate.now().plusDays(i), "thumnail" + i,
                    ViewRating.ALL, Genre.COMEDY, 60 + i, user, stadium);

            musicalRepository.save(musical);

            musicals.add(MusicalSummaryResponseDTO.from(musical));
        }
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        PageResponseDTO<Musical, MusicalSummaryResponseDTO> result = musicalFacadeService.findAllMusicals(pageable);

        // then
        for (int i = 0; i < musicals.size(); i++) {
            assertThat(musicals.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(result.getData().get(i));
        }
    }

    @Test
    @DisplayName("Success - 뮤지컬 상세 조회 시 뮤지컬 정보를 반환 - findMusicalById")
    void getMusicalSuccess() {
        // given
        User user = TestUtil.createUser(Social.GOOGLE, "socialId", "refreshToken", "nickname", UserRole.ADMIN, false, LocalDate.now(), "email@naver.com");
        Stadium stadium = TestUtil.createStadium("name", "address", "imageUrl");
        Musical musical = TestUtil.createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5),
                "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        MusicalDetailImage musicalDetailImage = TestUtil.createMusicalDetailImage("imageUrl", "fileName");
        musicalDetailImage.setMusical(musical);
        Actor actor = TestUtil.createActor("actorName", LocalDate.now(), "profileUrl", Gender.MALE);
        Casting casting = TestUtil.createCasting(actor, musical);
        casting.setActor(actor);
        casting.setMusical(musical);

        userRepository.save(user);
        stadiumRepository.save(stadium);
        musicalRepository.save(musical);
        musicalDetailImageRepository.save(musicalDetailImage);
        actorRepository.save(actor);
        castingRepository.save(casting);

        MusicalDetailResponseDTO answer = MusicalDetailResponseDTO.builder()
                .musicalTitle(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .rate(musical.getViewRating())
                .genre(musical.getGenre())
                .thumbnailUrl(musical.getThumbnailUrl())
                .description(musical.getDescription())
                .runningTime(musical.getRunningTime())
                .stadiumName(stadium.getName())
                .actors(ActorResponseDTO.listFromCastings(List.of(casting)))
                .images(MusicalDetailImageResponseDTO.listFrom(musical.getDetailImages()))
                .build();

        // when
        MusicalDetailResponseDTO result = musicalFacadeService.findMusicalById(musical.getId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(answer);

    }

    @Test
    @DisplayName("Fail - 뮤지컬 상세 조회 시 존재하지 않는 뮤지컬인 경우 조회 실패 - findMusicalById")
    void getMusicalFail() {
        // given
        Long notExistsMusicalId = 0L;

        // when, then
        Assertions.assertThrows(EntityNotFoundException.class, () -> musicalFacadeService.findMusicalById(notExistsMusicalId));
    }
}