package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.ActorProvider;
import com.prgrms.be.intermark.domain.util.SeatProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
import com.prgrms.be.intermark.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
    private SeatRepository seatRepository;

    @Autowired
    private ActorRepository actorRepository;

    private final User user = UserProvider.createUser();
    private final Stadium stadium = StadiumProvider.createStadium();
    private final Seat seat1 = SeatProvider.createSeat("A", 1, stadium);
    private final Seat seat2 = SeatProvider.createSeat("A", 2, stadium);
    private final Actor actor1 = ActorProvider.createActor("kwon", LocalDate.of(1997, 10, 10), Gender.MALE, "a");
    private final Actor actor2 = ActorProvider.createActor("kong", LocalDate.of(1996, 1, 1), Gender.MALE, "b");

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        stadiumRepository.save(stadium);
        seatRepository.save(seat1);
        seatRepository.save(seat2);
        actorRepository.save(actor1);
        actorRepository.save(actor2);
    }

    @Test
    @DisplayName("성공 - 정상 뮤지컬 등록 데이터를 입력하면 등록에 성공한다. - create")
    void createSuccess() throws IOException {
        // given
        MusicalSeatGradeCreateRequestDTO seatGradeVIP = MusicalSeatGradeCreateRequestDTO.builder()
                .seatGradeName("VIP")
                .seatGradePrice(50000)
                .build();

        MusicalSeatGradeCreateRequestDTO seatGradeR = MusicalSeatGradeCreateRequestDTO.builder()
                .seatGradeName("R")
                .seatGradePrice(30000)
                .build();

        MusicalSeatCreateRequestDTO musicalSeatVIP = MusicalSeatCreateRequestDTO.builder()
                .seatId(seat1.getId())
                .seatGradeName(seatGradeVIP.seatGradeName())
                .build();

        MusicalSeatCreateRequestDTO musicalSeatR = MusicalSeatCreateRequestDTO.builder()
                .seatId(seat2.getId())
                .seatGradeName(seatGradeR.seatGradeName())
                .build();

        MusicalCreateRequestDTO createRequestDTO = MusicalCreateRequestDTO.builder()
                .title("마르코팀 성장기")
                .viewRating(ViewRating.ADULT)
                .genre(Genre.DRAMA)
                .description("마르코팀의 성장기입니다.")
                .startDate(LocalDate.of(2022, 10, 12))
                .endDate(LocalDate.of(2023, 3, 15))
                .runningTime(100)
                .managerId(user.getId())
                .stadiumId(stadium.getId())
                .actorIds(List.of(actor1.getId(), actor2.getId()))
                .seatGrades(List.of(seatGradeVIP, seatGradeR))
                .seats(List.of(musicalSeatVIP, musicalSeatR))
                .build();

        MockMultipartFile thumbnail = getMockMultipartFile("testMusicalThumbnail", "png",
                "src/test/resources/testMusicalThumbnail.jpg");
        MockMultipartFile detailImage1 = getMockMultipartFile("testMusicalDetailImage1", "png",
                "src/test/resources/testMusicalDetailImage1.jpg");
        MockMultipartFile detailImage2 = getMockMultipartFile("testMusicalDetailImage2", "png",
                "src/test/resources/testMusicalDetailImage2.jpg");

        // when
        Long musicalId = musicalFacadeService.create(createRequestDTO,
                thumbnail, List.of(detailImage1, detailImage2));

        // then
        assertThat(musicalId).isNotNull();
    }

    @Test
    @DisplayName("Success - 뮤지컬 리스트 조회 시 뮤지컬 정보 리스트로 반환 - findAllMusicals")
    void getAllMusicalsSuccess() {
        // given
        User user = TestUtil.createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
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

    private MockMultipartFile getMockMultipartFile(String fileName, String extension, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName + "." + extension, extension, fileInputStream);
    }

}