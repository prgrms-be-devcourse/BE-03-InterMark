package com.prgrms.be.intermark.domain.ticket.service;

import static com.prgrms.be.intermark.util.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByMusicalDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseByUserDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketResponseDTO;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MusicalRepository musicalRepository;

    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PageService pageService;

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
    List<Ticket> tickets;

    @BeforeEach
    void init() {
        user = createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
        stadium = createStadium("name", "address", "imageUrl");
        musical = createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5), "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        seat = createSeat("A", 1, stadium);
        seatGrade = createSeatGrade("VIP", 10000, musical);
        schedule = createSchedule(LocalDateTime.now(), LocalDateTime.now().plusHours(2), musical);
        scheduleSeat = createScheduleSeat(false, seat, seatGrade, schedule);
        tickets = List.of(
                Ticket.builder()
                        .user(user)
                        .schedule(schedule)
                        .seat(Seat.builder().build())
                        .seatGrade(SeatGrade.builder().build())
                        .musical(musical)
                        .stadium(stadium).build(),
                Ticket.builder()
                        .user(user)
                        .schedule(schedule)
                        .seat(Seat.builder().build())
                        .seatGrade(SeatGrade.builder().build())
                        .musical(musical)
                        .stadium(stadium).build()
        );
    }

    @Test
    @DisplayName("Success - ?????? ?????? ??? Ticket ???????????? ?????? ??? ScheduleSeat ?????? ?????? ?????? - createTicket")
    void createTicketSuccess() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetchWithLock(request.scheduleSeatId()))
                .thenReturn(Optional.of(scheduleSeat));
        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(any(Ticket.class));

        // when
        ticketService.createTicket(request);

        // then
        verify(userRepository).findByIdAndIsDeletedFalse(request.userId());
        verify(scheduleSeatRepository).findByScheduleSeatFetchWithLock(request.scheduleSeatId());
        verify(ticketRepository).save(any(Ticket.class));
        assertThat(scheduleSeat.isReserved()).isTrue();
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ???????????? ?????? ????????? ?????? EntityNotFoundException ?????? - createTicket")
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
    @DisplayName("Fail - ?????? ?????? ??? ???????????? ?????? ??????????????? ?????? EntityNotFoundException ?????? - createTicket")
    void createTicketNotExistsScheduleSeatFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetchWithLock(request.userId()))
                .thenThrow(EntityNotFoundException.class);

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ?????? ????????? ScheduleSeat ??? ?????? IllegarArgumentException ?????? - createTicket")
    void createTicketIsReservedFail() {
        // given
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .userId(1L)
                .scheduleSeatId(1L)
                .build();

        ScheduleSeat isReservedScheduleSeat = createScheduleSeat(true, seat, seatGrade, schedule);

        when(userRepository.findByIdAndIsDeletedFalse(request.userId()))
                .thenReturn(Optional.of(user));
        when(scheduleSeatRepository.findByScheduleSeatFetchWithLock(request.scheduleSeatId()))
                .thenReturn(Optional.of(isReservedScheduleSeat));

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ????????? ???????????????.");
    }

    @Test
    @DisplayName("Fail - ?????? ?????? ??? ?????? ???????????? ????????? ???????????? ?????? IllegarArgumentException ?????? - createTicket")
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
        when(scheduleSeatRepository.findByScheduleSeatFetchWithLock(request.scheduleSeatId()))
                .thenReturn(Optional.of(pastScheduleSeat));

        // when, then
        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ?????? ??????????????????.");
    }

    @Test
    @DisplayName("Success - ?????? ????????? ?????? ???????????? ?????? ?????? ?????? ????????? ??????")
    void findAllTicketsSuccess() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        PageImpl<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest, tickets.size());

        PageResponseDTO<Ticket, TicketResponseDTO> ticketsResponseDto = new PageResponseDTO<>(
                ticketPage, TicketResponseDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);

        when(ticketRepository.findAll(pageRequest)).thenReturn(ticketPage);
        when(ticketRepository.count()).thenReturn((long) tickets.size());
        when(pageService.getPageRequest(pageRequest, tickets.size())).thenReturn(pageRequest);

        // when
        PageResponseDTO<Ticket, TicketResponseDTO> responseDto = ticketService.getAllTickets(pageRequest);

        // then
        verify(ticketRepository).findAll(pageRequest);
        verify(ticketRepository).count();
        verify(pageService).getPageRequest(pageRequest, tickets.size());
        assertThat(responseDto.getData()).isEqualTo(ticketsResponseDto.getData());
        assertThat(responseDto.getNowPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("Success - ????????? ????????? ???????????? ?????? ????????? ????????? ?????? ?????? ????????? ??????")
    void findTicketsByUserSuccess() {
        // given
        PageRequest pageRequest = PageRequest.of(0, tickets.size());
        PageImpl<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest, tickets.size());

        PageResponseDTO<Ticket, TicketResponseByUserDTO> ticketsResponseDto = new PageResponseDTO<>(
                ticketPage, TicketResponseByUserDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(ticketRepository.findByUser(user, pageRequest)).thenReturn(ticketPage);
        when(ticketRepository.countByUser(user)).thenReturn((long) tickets.size());
        when(pageService.getPageRequest(pageRequest, tickets.size())).thenReturn(pageRequest);

        // when
        PageResponseDTO<Ticket, TicketResponseByUserDTO> responseDto = ticketService.getTicketsByUser(
                user.getId(), pageRequest);

        // then
        assertThat(responseDto.getData()).isEqualTo(ticketsResponseDto.getData());
        assertThat(responseDto.getNowPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fail - ???????????? ?????? ????????? ????????? ???????????? EntityNotFoundException ??????")
    void findTicketsByUserFail() {
        // given
        PageRequest pageRequest = PageRequest.of(0, tickets.size());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> ticketService.getTicketsByUser(user.getId(), pageRequest))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("?????? ????????? ???????????? ????????????.");
    }

    @Test
    @DisplayName("Success - ???????????? ????????? ???????????? ?????? ???????????? ?????? ?????? ????????? ??????")
    void findTicketsByMusicalSuccess() {
        // given
        PageRequest pageRequest = PageRequest.of(0, tickets.size());
        PageImpl<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest, tickets.size());
        PageResponseDTO<Ticket, TicketResponseByMusicalDTO> ticketsResponseDto = new PageResponseDTO<>(
                ticketPage, TicketResponseByMusicalDTO::from, PageListIndexSize.TICKET_LIST_INDEX_SIZE);

        when(musicalRepository.findById(musical.getId())).thenReturn(Optional.of(musical));
        when(ticketRepository.findByMusical(musical, pageRequest)).thenReturn(ticketPage);
        when(ticketRepository.countByMusical(musical)).thenReturn((long) tickets.size());
        when(pageService.getPageRequest(pageRequest, tickets.size())).thenReturn(pageRequest);

        // when
        PageResponseDTO<Ticket, TicketResponseByMusicalDTO> responseDto = ticketService.getTicketsByMusical(
                musical.getId(), pageRequest);

        // then
        assertThat(responseDto.getData()).isEqualTo(ticketsResponseDto.getData());
        assertThat(responseDto.getNowPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fail - ???????????? ?????? ???????????? ????????? ???????????? EntityNotFoundException ??????")
    void findTicketsByMusicalFail() {
        // given
        PageRequest pageRequest = PageRequest.of(0, tickets.size());

        when(musicalRepository.findById(musical.getId())).thenReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> ticketService.getTicketsByMusical(musical.getId(), pageRequest))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("?????? ???????????? ???????????? ????????????.");
    }

    @Test
    @DisplayName("Success - ????????? ???????????? ?????? ?????? ??????")
    void findTicketSuccess() {
        // given
        Ticket ticket = tickets.get(0);

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        // when
        TicketResponseDTO responseDto = ticketService.getTicketById(ticket.getId());

        // then
        verify(ticketRepository).findById(ticket.getId());
        assertThat(responseDto).isEqualTo(TicketResponseDTO.from(ticket));
    }

    @Test
    @DisplayName("Fail - ???????????? ?????? ????????? ???????????? EntityNotFoundException ??????")
    void findTicketFail() {
        // given
        long notExistedTicketId = 0L;
        when(ticketRepository.findById(notExistedTicketId)).thenReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> ticketService.getTicketById(notExistedTicketId))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("?????? ????????? ???????????? ????????????.");

        verify(ticketRepository).findById(notExistedTicketId);
    }

    @Test
    @DisplayName("Success - ????????? ???????????? isDeleted = true ??? ??????")
    void deleteTicketSuccess() {
        // given
        Ticket ticket = tickets.get(0);

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        // when
        ticketService.deleteTicket(ticket.getId());

        // then
        verify(ticketRepository).findById(ticket.getId());
        assertThat(ticket.isDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("Fail - ???????????? ?????? ????????? ???????????? EntityNotFoundException ??????")
    void deleteTicketFail() {
        // given
        long notExistedTicketId = 0L;
        when(ticketRepository.findById(notExistedTicketId)).thenReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> ticketService.deleteTicket(notExistedTicketId))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("???????????? ?????? ???????????????.");

        verify(ticketRepository).findById(notExistedTicketId);
    }

    @Test
    @DisplayName("Success - ?????? ???????????? ?????? ????????? ???????????? ??? ?????? ?????? ??????")
    void isExistTicketByMusical() {
        // given
        when(ticketRepository.existsByMusical(musical)).thenReturn(true);

        // when
        boolean isExist = ticketService.existsByMusical(musical);

        // then
        assertThat(isExist).isEqualTo(true);

        @Nested
        @DisplayName("deleteTicket")
        class DeleteTicket {

            @Test
            @DisplayName("Success - ?????? ?????? ?????? id ??? ???????????? ????????? ????????????.")
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
            @DisplayName("Fail - ?????? ?????? ?????? id ??? ????????? ????????? ????????? ????????????.")
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
            @DisplayName("Fail - ?????? ?????? ?????? id ??? ?????? ?????????????????? ????????? ????????? ????????????.")
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
}