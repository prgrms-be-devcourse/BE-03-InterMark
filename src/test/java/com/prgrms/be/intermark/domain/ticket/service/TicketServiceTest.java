package com.prgrms.be.intermark.domain.ticket.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.prgrms.be.intermark.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private Ticket savedTicket;

    @Mock
    private ScheduleSeat savedScheduleSeat;

    User user;
    Stadium stadium;
    Musical musical;
    Seat seat;
    SeatGrade seatGrade;
    Schedule schedule;
    ScheduleSeat scheduleSeat;

    @BeforeEach
    void init() {
        user = createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
        stadium = createStadium("name", "address", "imageUrl");
        musical = createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5), "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        seat = createSeat("A", 1, stadium);
        seatGrade = createSeatGrade("VIP", 10000, musical);
        schedule = createSchedule(LocalDateTime.now(), LocalDateTime.now().plusHours(2), musical);
        scheduleSeat = createScheduleSeat(false, seat, seatGrade, schedule);
    }

    @Test
    @DisplayName("Success - 예매 등록 시 Ticket 저장로직 호출 및 ScheduleSeat 예매 상태 변경 - createTicket")
    void createTicketSuccess() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetch(request.scheduleSeatId()))
                .thenReturn(Optional.of(scheduleSeat));
        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(any(Ticket.class));

        // when
        ticketService.createTicket(request);

        // then
        verify(userRepository).findByIdAndIsDeletedFalse(request.userId());
        verify(scheduleSeatRepository).findByScheduleSeatFetch(request.scheduleSeatId());
        verify(ticketRepository).save(any(Ticket.class));
        assertThat(scheduleSeat.isReserved()).isTrue();
    }

    @Test
    @DisplayName("Fail - 예매 등록 시 존재하지 않는 유저의 경우 EntityNotFoundException 발생 - createTicket")
    void createTicketNotExistsUserFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenThrow(EntityNotFoundException.class);

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Fail - 예매 등록 시 존재하지 않는 스케줄좌석 경우 EntityNotFoundException 발생 - createTicket")
    void createTicketNotExistsScheduleSeatFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetch(request.userId()))
                .thenThrow(EntityNotFoundException.class);

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Fail - 예매 등록 시 이미 예약된 ScheduleSeat 인 경우 IllegarArgumentException 발생 - createTicket")
    void createTicketIsReservedFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        ScheduleSeat isReservedScheduleSeat = createScheduleSeat(true, seat, seatGrade, schedule);

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetch(request.scheduleSeatId()))
                .thenReturn(Optional.of(isReservedScheduleSeat));

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 예약된 좌석입니다.");
    }

    @Test
    @DisplayName("Fail - 예매 등록 시 현재 날짜보다 이전의 스케줄인 경우 IllegarArgumentException 발생 - createTicket")
    void createTicketIsOverFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        Schedule pastSchedule = createSchedule(LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(8), musical);
        ScheduleSeat pastScheduleSeat = createScheduleSeat(false, seat, seatGrade, pastSchedule);

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetch(request.scheduleSeatId()))
                .thenReturn(Optional.of(pastScheduleSeat));

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 지난 스케줄입니다.");
    }

    @Nested
    @DisplayName("deleteTicket")
    class DeleteTicket {

        @Test
        @DisplayName("Success - 입력 받은 티켓 id 에 해당하는 티켓을 환불한다.")
        void deleteTicketSuccess() {
            // given
            Long ticketId = 1L;
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(savedTicket));
            when(savedTicket.isDeleted()).thenReturn(false);
            when(savedTicket.getSchedule()).thenReturn(schedule);
            when(savedTicket.getSeat()).thenReturn(seat);
            when(scheduleSeatRepository.findByScheduleAndSeat(schedule, seat)).thenReturn(Optional.of(savedScheduleSeat));
            doNothing().when(savedScheduleSeat).refund();
            doNothing().when(savedTicket).deleteTicket();

            // when
            ticketService.deleteTicket(ticketId);

            // then
            verify(ticketRepository).findById(ticketId);
            verify(savedTicket).isDeleted();
            verify(savedTicket).getSchedule();
            verify(savedTicket).getSeat();
            verify(scheduleSeatRepository).findByScheduleAndSeat(schedule, seat);
            verify(savedScheduleSeat).refund();
            verify(savedTicket).deleteTicket();
        }

        @Test
        @DisplayName("Fail - 입력 받은 티켓 id 가 없으면 티켓을 환불에 실패한다.")
        void deleteTicketFailByNoTicket() {
            // given
            Long ticketId = 1L;
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> ticketService.deleteTicket(ticketId))
                    .isExactlyInstanceOf(EntityNotFoundException.class);
            verify(ticketRepository).findById(ticketId);
        }

        @Test
        @DisplayName("Fail - 입력 받은 티켓 id 가 이미 환불되었으면 티켓을 환불에 실패한다.")
        void deleteTicketFailByAlreadyDelete() {
            // given
            Long ticketId = 1L;
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(savedTicket));
            when(savedTicket.isDeleted()).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> ticketService.deleteTicket(ticketId))
                    .isExactlyInstanceOf(EntityNotFoundException.class);
            verify(ticketRepository).findById(ticketId);
            verify(savedTicket).isDeleted();
        }
    }
}