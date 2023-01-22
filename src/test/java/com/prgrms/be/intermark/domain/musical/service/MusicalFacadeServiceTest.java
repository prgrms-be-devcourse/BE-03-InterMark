package com.prgrms.be.intermark.domain.musical.service;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCommandResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
class MusicalFacadeServiceTest {

	@Autowired
	private MusicalFacadeService musicalFacadeService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StadiumRepository stadiumRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private ActorRepository actorRepository;

	private User user;
	private Stadium stadium;
	private Seat seat1;
	private Seat seat2;
	private Actor actor1;
	private Actor actor2;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.birth(LocalDate.of(1997, 10, 10))
			.social(Social.GOOGLE)
			.socialId("intermark")
			.refreshToken("abcdefg")
			.nickname("인터마크 관리자")
			.role(UserRole.ADMIN)
			.isDeleted(false)
			.email("intermark@gmail.com")
			.build();
		stadium = Stadium.builder()
			.name("예술의 전당")
			.address("서울특별시")
			.imageUrl("abcdefg")
			.build();
		seat1 = Seat.builder()
			.stadium(stadium)
			.rowNum("A")
			.columnNum(1)
			.build();
		seat2 = Seat.builder()
			.stadium(stadium)
			.rowNum("A")
			.columnNum(2)
			.build();
		actor1 = Actor.builder()
			.birth(LocalDate.of(1997, 10, 10))
			.name("kwon")
			.gender(Gender.MALE)
			.profileImageUrl("kwon/image")
			.build();
		actor2 = Actor.builder()
			.birth(LocalDate.of(1996,1, 1))
			.name("kong")
			.gender(Gender.MALE)
			.profileImageUrl("kong/image")
			.build();

		userRepository.save(user);
		stadiumRepository.save(stadium);
		seatRepository.save(seat1);
		seatRepository.save(seat2);
		actorRepository.save(actor1);
		actorRepository.save(actor2);
	}

	@Test
	@DisplayName("create - 뮤지컬 등록 데이터를 입력하면 등록에 성공한다. - 성공")
	void create() throws IOException {
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
			.actorIds(List.of(1L, 2L))
			.seatGrades(List.of(seatGradeVIP, seatGradeR))
			.seats(List.of(musicalSeatVIP, musicalSeatR))
			.build();

		MockMultipartFile thumbnail = getMockMultipartFile("testMusicalThumbnail", "png",
			"src/test/resources/testMusicalThumbnail.png");
		MockMultipartFile detailImage1 = getMockMultipartFile("testMusicalDetailImage1", "png",
			"src/test/resources/testMusicalDetailImage1.png");
		MockMultipartFile detailImage2 = getMockMultipartFile("testMusicalDetailImage2", "png",
			"src/test/resources/testMusicalDetailImage2.png");

		// when
		MusicalCommandResponseDTO musicalResponseDTO = musicalFacadeService.create(createRequestDTO,
			thumbnail, List.of(detailImage1, detailImage2));

		// then
		assertThat(musicalResponseDTO.id()).isNotNull();
	}

	private MockMultipartFile getMockMultipartFile(String fileName, String extension, String path) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(path));
		return new MockMultipartFile(fileName, fileName + "." + extension, extension, fileInputStream);
	}

}