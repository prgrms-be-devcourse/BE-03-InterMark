package com.prgrms.be.intermark.domain.ticket.service;

import static com.prgrms.be.intermark.util.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

@Transactional
@SpringBootTest
class TicketServiceIntegrationTest {

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

    @Test
    @DisplayName("Success - ?????? ?????? ??? Ticket ???????????? ?????? ??? ScheduleSeat ?????? ?????? ?????? - createTicket")
    void createTicketSuccess() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(user.getId())
                .scheduleSeatId(scheduleSeat.getId())
                .build();

        // when
        Long ticketId = ticketService.createTicket(request);
        Ticket ticket = ticketRepository.findById(ticketId).get();

        // then
        assertThat(ticketId).isEqualTo(ticket.getId());
        assertThat(request.userId()).isEqualTo(ticket.getUser().getId());
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ???????????? ?????? ????????? ?????? EntityNotFoundException ?????? - createTicket")
    void createTicketNotExistsUserFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(0L)
                .scheduleSeatId(scheduleSeat.getId())
                .build();

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("???????????? ?????? ???????????????.");
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ???????????? ?????? ??????????????? ?????? EntityNotFoundException ?????? - createTicket")
    void createTicketNotExistsScheduleSeatFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(user.getId())
                .scheduleSeatId(0L)
                .build();

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ?????? ????????? ScheduleSeat ??? ?????? IllegarArgumentException ?????? - createTicket")
    void createTicketIsReservedFail() {
        // given
        scheduleSeat.reserve();

        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(user.getId())
                .scheduleSeatId(scheduleSeat.getId())
                .build();

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ????????? ???????????????.");
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ?????? ???????????? ????????? ???????????? ?????? IllegarArgumentException ?????? - createTicket")
    void createTicketIsOverFail() {
        // given
        schedule.setScheduleTime(LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(8));

        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(user.getId())
                .scheduleSeatId(scheduleSeat.getId())
                .build();

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ?????? ??????????????????.");
    }

    @Nested
    @DisplayName("deleteTicket")
    class DeleteTicket {

        @Test
        @DisplayName("Success - ?????? ?????? ?????? id ??? ???????????? ????????? ????????????.")
        void deleteTicketSuccess() {
            // given
            TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                    .userId(user.getId())
                    .scheduleSeatId(scheduleSeat.getId())
                    .build();
            Long ticketId = ticketService.createTicket(request);
            Ticket ticket = ticketRepository.findById(ticketId).get();

            // when
            ticketService.deleteTicket(ticketId);

            // then
            assertThat(ticket.isDeleted()).isTrue();
            assertThat(scheduleSeat.isReserved()).isFalse();
        }

        @Test
        @DisplayName("Fail - ?????? ?????? ?????? id ??? ????????? ????????? ????????? ????????????.")
        void deleteTicketFailByNoTicket() {
            // given
            Long notExistId = 0L;

            // when & then
            assertThatThrownBy(() -> ticketService.deleteTicket(notExistId))
                    .isExactlyInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Fail - ?????? ?????? ?????? id ??? ?????? ?????????????????? ????????? ????????? ????????????.")
        void deleteTicketFailByAlreadyDelete() {
            // given
            TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                    .userId(user.getId())
                    .scheduleSeatId(scheduleSeat.getId())
                    .build();
            Long ticketId = ticketService.createTicket(request);
            ticketService.deleteTicket(ticketId);

            // when & then
            assertThatThrownBy(() -> ticketService.deleteTicket(ticketId))
                    .isExactlyInstanceOf(EntityNotFoundException.class);

        }
    }
}
