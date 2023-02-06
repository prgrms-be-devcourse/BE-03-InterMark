package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.prgrms.be.intermark.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TicketServiceConcurrencyIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    @Autowired
    private MusicalSeatRepository musicalSeatRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    User user;
    Stadium stadium;
    Musical musical;
    Seat seat;
    SeatGrade seatGrade;
    MusicalSeat musicalSeat;
    Schedule schedule;
    ScheduleSeat scheduleSeat;

    @BeforeEach
    void init() {
        user = createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
        stadium = createStadium("name", "address", "imageUrl");
        musical = createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5), "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        seat = createSeat("A", 1, stadium);
        seatGrade = createSeatGrade("VIP", 10000, musical);
        musicalSeat = createMusicalSeat(musical, seat, seatGrade);
        schedule = createSchedule(LocalDateTime.now(), LocalDateTime.now().plusHours(2), musical);
        scheduleSeat = createScheduleSeat(false, seat, seatGrade, schedule);

        userRepository.save(user);
        stadiumRepository.save(stadium);
        musicalRepository.save(musical);
        seatRepository.save(seat);
        seatGradeRepository.save(seatGrade);
        scheduleRepository.save(schedule);
        scheduleSeatRepository.save(scheduleSeat);
        musicalSeatRepository.save(musicalSeat);
    }

    @AfterEach
    void clear() {
        ticketRepository.deleteAll();
        scheduleSeatRepository.deleteAll();
        musicalSeatRepository.deleteAll();
        scheduleRepository.deleteAll();
        seatGradeRepository.deleteAll();
        seatRepository.deleteAll();
        musicalRepository.deleteAll();
        stadiumRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Success - 50명이 동시에 같은 스케줄좌석에 예매했을 때 1명만 예매에 성공해야 한다. - createTicket")
    void createTicketConcurrencySuccess() throws InterruptedException {

        // given
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(user.getId())
                .scheduleSeatId(scheduleSeat.getId())
                .build();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    ticketService.createTicket(request);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(1);
    }
}
