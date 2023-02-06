package com.prgrms.be.intermark.domain.ticket.repository;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.model.TicketStatus;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private StadiumRepository stadiumRepository;
    @Autowired
    private MusicalRepository musicalRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatGradeRepository seatGradeRepository;

    private final User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1")
            .role(UserRole.ROLE_USER)
            .nickname("유저")
            .email("example1@gmail.com")
            .build();

    private final Stadium stadium = Stadium.builder()
            .name("stadium")
            .address("Korea Seoul")
            .imageUrl("image")
            .build();

    private final Musical musical = Musical.builder()
            .title("title")
            .thumbnailUrl("thumbnail")
            .viewRating(ViewRating.ALL)
            .genre(Genre.COMEDY)
            .description("description")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .runningTime(80)
            .stadium(stadium)
            .user(user)
            .build();

    Schedule schedule = Schedule.builder()
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusMinutes(musical.getRunningTime()))
            .musical(musical)
            .build();

    Seat seat = Seat.builder()
            .rowNum("A")
            .columnNum(1)
            .stadium(stadium)
            .build();
    SeatGrade seatGrade = SeatGrade.builder()
            .name("좌석 등급")
            .price(10000)
            .musical(musical)
            .build();

    List<Ticket> tickets = List.of(
            Ticket.builder()
                    .user(user)
                    .schedule(schedule)
                    .seat(seat)
                    .seatGrade(seatGrade)
                    .musical(musical)
                    .stadium(stadium)
                    .ticketStatus(TicketStatus.AVAILABLE).build(),
            Ticket.builder()
                    .user(user)
                    .schedule(schedule)
                    .seat(seat)
                    .seatGrade(seatGrade)
                    .musical(musical)
                    .stadium(stadium)
                    .ticketStatus(TicketStatus.AVAILABLE).build()
    );

    @BeforeEach
    void init() {
        userRepository.save(user);
        stadiumRepository.save(stadium);
        musicalRepository.save(musical);
        scheduleRepository.save(schedule);
        seatRepository.save(seat);
        seatGradeRepository.save(seatGrade);
    }

    @Test
    @DisplayName("Success - 유저로 티켓을 조회하면 해당 유저가 예매한 티켓 페이지 반환")
    @Transactional
    void findByUserSuccess() {
        // given
        ticketRepository.saveAll(tickets);
        PageRequest pageRequest = PageRequest.of(0, 2);
        PageImpl<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest, tickets.size());

        // when
        Page<Ticket> foundTickets = ticketRepository.findByUser(user, pageRequest);

        // then
        assertThat(foundTickets).isEqualTo(ticketPage);
    }

    @Test
    @DisplayName("Success - 유저가 예매한 티켓의 개수를 조회하면 티켓의 개수 반환")
    void countByUserSuccess() {
        // given
        ticketRepository.saveAll(tickets);

        // when
        long usersCnt = ticketRepository.countByUser(user);

        // then
        List<Ticket> ticketsByUser = tickets.stream().filter(ticket -> ticket.getUser() == user).toList();
        assertThat(usersCnt).isEqualTo(ticketsByUser.size());
    }

    @Test
    @DisplayName("Success - 뮤지컬로 티켓을 조회하면 해당 뮤지컬 티켓 페이지 반환")
    @Transactional
    void findByMusicalSuccess() {
        // given
        ticketRepository.saveAll(tickets);
        PageRequest pageRequest = PageRequest.of(0, 2);
        PageImpl<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest, tickets.size());

        // when
        Page<Ticket> foundTickets = ticketRepository.findByMusical(musical, pageRequest);

        // then
        assertThat(foundTickets).isEqualTo(ticketPage);
    }

    @Test
    @DisplayName("Success - 해당 뮤지컬의 티켓의 개수를 조회하면 티켓의 개수 반환")
    void countByMusicalSuccess() {
        // given
        ticketRepository.saveAll(tickets);

        // when
        long usersCnt = ticketRepository.countByMusical(musical);

        // then
        List<Ticket> ticketsByMusical = tickets.stream().filter(ticket -> ticket.getMusical() == musical).toList();
        assertThat(usersCnt).isEqualTo(ticketsByMusical.size());
    }

    @Test
    @DisplayName("Success - 특정 뮤지컬에 대한 예매의 존재 여부를 조회하면 참 또는 거짓 반환")
    void isExistTicketByMusical() {
        // given
        Ticket ticket = tickets.get(0);
        ticketRepository.save(ticket);

        // when
        boolean isExist = ticketRepository.existsByMusical(musical);

        // then
        assertThat(isExist).isEqualTo(true);
    }
}