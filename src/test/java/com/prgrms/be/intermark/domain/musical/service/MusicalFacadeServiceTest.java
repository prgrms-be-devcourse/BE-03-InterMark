package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.actor.dto.ActorResponseDTO;
import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.dto.*;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalDetailImageRepository;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.*;
import com.prgrms.be.intermark.util.TestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CastingRepository castingRepository;

    @Autowired
    private MusicalDetailImageRepository musicalDetailImageRepository;

    private User user;
    private Stadium stadium;
    private Seat seat1;
    private Seat seat2;
    private Actor actor1;
    private Actor actor2;
    private MusicalSeatGradeCreateRequestDTO seatGradeVIP;
    private MusicalSeatGradeCreateRequestDTO seatGradeR;
    private MusicalSeatCreateRequestDTO musicalSeatVIP;
    private MusicalSeatCreateRequestDTO musicalSeatR;
    private MusicalCreateRequestDTO createRequestDTO;
    private MockMultipartFile thumbnail;
    private MockMultipartFile detailImage1;
    private MockMultipartFile detailImage2;

    @BeforeEach
    void setUp() throws IOException {
        user = UserProvider.createUser();
        stadium = StadiumProvider.createStadium();
        seat1 = SeatProvider.createSeat("A", 1, stadium);
        seat2 = SeatProvider.createSeat("A", 2, stadium);
        actor1 = ActorProvider.createActor();
        actor2 = ActorProvider.createActor();

        userRepository.save(user);
        stadiumRepository.save(stadium);
        seatRepository.save(seat1);
        seatRepository.save(seat2);
        actorRepository.save(actor1);
        actorRepository.save(actor2);

        seatGradeVIP = MusicalSeatGradeCreateRequestDTO.builder()
                .seatGradeName("VIP")
                .seatGradePrice(50000)
                .build();
        seatGradeR = MusicalSeatGradeCreateRequestDTO.builder()
                .seatGradeName("R")
                .seatGradePrice(30000)
                .build();
        musicalSeatVIP = MusicalSeatCreateRequestDTO.builder()
                .seatId(seat1.getId())
                .seatGradeName(seatGradeVIP.seatGradeName())
                .build();
        musicalSeatR = MusicalSeatCreateRequestDTO.builder()
                .seatId(seat2.getId())
                .seatGradeName(seatGradeR.seatGradeName())
                .build();
        createRequestDTO = MusicalCreateRequestDTO.builder()
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
        thumbnail = getMockMultipartFile("testMusicalThumbnail", "png",
                "src/test/resources/testMusicalThumbnail.jpg");
        detailImage1 = getMockMultipartFile("testMusicalDetailImage1", "png",
                "src/test/resources/testMusicalDetailImage1.jpg");
        detailImage2 = getMockMultipartFile("testMusicalDetailImage2", "png",
                "src/test/resources/testMusicalDetailImage2.jpg");
    }

    @Test
    @DisplayName("성공 - 정상 뮤지컬 등록 데이터를 입력하면 등록에 성공한다. - create")
    void createSuccess() {

        // given & when
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

    @Test
    @DisplayName("Success - 뮤지컬 상세 조회 시 뮤지컬 정보를 반환 - findMusicalById")
    void getMusicalSuccess() {
        // given
        User user = TestUtil.createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
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

    @Nested
    @DisplayName("deleteMusical")
    class DeleteMusical {

        @Test
        @DisplayName("Success - 입력 받은 뮤지컬 id 에 해당하는 뮤지컬 삭제에 성공한다.")
        void deleteMusicalSuccess() {
            // given
            Long musicalId = musicalFacadeService.create(createRequestDTO, thumbnail, List.of(detailImage1, detailImage2));
            Musical savedMusical = musicalRepository.findById(musicalId).get();

            // when
            musicalFacadeService.deleteMusical(musicalId);

            // then
            assertThat(savedMusical.isDeleted()).isTrue();
            assertThat(savedMusical.getCastings())
                    .extracting(Casting::isDeleted)
                    .allMatch(isDeleted -> isDeleted == true);
            assertThat(savedMusical.getDetailImages())
                    .extracting(MusicalDetailImage::isDeleted)
                    .allMatch(isDeleted -> isDeleted == true);
            assertThat(savedMusical.getSchedules())
                    .extracting(Schedule::isDeleted)
                    .allMatch(isDeleted -> isDeleted == true);
            assertThat(savedMusical.getSeatGrades())
                    .extracting(SeatGrade::isDeleted)
                    .allMatch(isDeleted -> isDeleted == true);
            assertThat(savedMusical.getMusicalSeats())
                    .extracting(MusicalSeat::isDeleted)
                    .allMatch(isDeleted -> isDeleted == true);
        }

        @Test
        @DisplayName("Fail - 삭제하려는 뮤지컬에 예매된 티켓이 있다면 삭제에 실패한다.")
        void deleteMusicalFail() throws IOException {
            // given
            Long musicalId = musicalFacadeService.create(createRequestDTO, thumbnail, List.of(detailImage1, detailImage2));
            Musical savedMusical = musicalRepository.findById(musicalId).get();

            Schedule schedule = ScheduleProvider.createSchedule(savedMusical);
            scheduleRepository.save(schedule);

            Ticket ticket = Ticket.builder()
                    .seatGrade(savedMusical.getSeatGrades().get(0))
                    .musical(savedMusical)
                    .seat(seat1)
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .stadium(stadium)
                    .user(user)
                    .schedule(schedule)
                    .build();
            ticket.setMusical(savedMusical);
            ticketRepository.save(ticket);

            // when & then
            assertThatThrownBy(() -> musicalFacadeService.deleteMusical(musicalId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String extension, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName + "." + extension, extension, fileInputStream);
    }
}