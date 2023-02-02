package com.prgrms.be.intermark.domain.ticket.model;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.prgrms.be.intermark.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class TicketTest {

    @Test
    @DisplayName("Success - 티켓을 삭제하면 티켓 상태가 취소됨으로 변경된다. - deleteTicket")
    void deleteTicketSuccess() {
        // given
        User user = createUser(SocialType.GOOGLE, "socialId", "nickname", UserRole.ROLE_ADMIN, false, LocalDate.now(), "email@naver.com");
        Stadium stadium = createStadium("name", "address", "imageUrl");
        Musical musical = createMusical("title", "description", LocalDate.now(), LocalDate.now().plusDays(5), "thumbnailUrl", ViewRating.ALL, Genre.COMEDY, 60, user, stadium);
        Seat seat = createSeat("A", 1, stadium);
        SeatGrade seatGrade = createSeatGrade("VIP", 10000, musical);
        Schedule schedule = createSchedule(LocalDateTime.now(), LocalDateTime.now().plusHours(2), musical);

        Ticket ticket = Ticket.builder()
                .musical(musical)
                .schedule(schedule)
                .stadium(stadium)
                .seat(seat)
                .seatGrade(seatGrade)
                .ticketStatus(TicketStatus.AVAILABLE)
                .user(user)
                .build();

        // when
        ticket.deleteTicket();

        // then
        assertThat(ticket.isDeleted()).isTrue();
    }
}