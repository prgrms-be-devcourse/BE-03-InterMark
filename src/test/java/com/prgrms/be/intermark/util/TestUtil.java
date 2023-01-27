package com.prgrms.be.intermark.util;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.schedule.model.Schedule;
import com.prgrms.be.intermark.domain.schedule_seat.model.ScheduleSeat;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestUtil {

    public static Musical createMusical(String title, String description, LocalDate startDate, LocalDate endDate, String thumbnailUrl, ViewRating viewRating,
                                        Genre genre, int runningTime, User user, Stadium stadium) {
        return Musical.builder()
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .thumbnailUrl(thumbnailUrl)
                .viewRating(viewRating)
                .genre(genre)
                .runningTime(runningTime)
                .user(user)
                .stadium(stadium)
                .build();
    }

    public static User createUser(Social social, String socialId, String refreshToken, String nickname, UserRole role, boolean isDeleted, LocalDate birth, String email) {
        return User.builder()
                .social(social)
                .socialId(socialId)
                .refreshToken(refreshToken)
                .nickname(nickname)
                .role(role)
                .isDeleted(isDeleted)
                .birth(birth)
                .email(email)
                .build();
    }


    public static Stadium createStadium(String name, String address, String imageUrl) {
        return Stadium.builder()
                .name(name)
                .address(address)
                .imageUrl(imageUrl)
                .build();
    }

    public static Actor createActor(String name, LocalDate birth, String profileImageUrl, Gender gender) {
        return Actor.builder()
                .name(name)
                .birth(birth)
                .profileImageUrl(profileImageUrl)
                .gender(gender)
                .build();
    }

    public static Casting createCasting(Actor actor, Musical musical) {
        return Casting.builder()
                .actor(actor)
                .musical(musical)
                .build();
    }

    public static MusicalDetailImage createMusicalDetailImage(String imageUrl, String originalFileName) {
        return MusicalDetailImage.builder()
                .imageUrl(imageUrl)
                .originalFileName(originalFileName)
                .build();
    }

    public static MusicalSeat createMusicalSeat(Musical musical, Seat seat, SeatGrade seatGrade) {
        return MusicalSeat.builder()
                .musical(musical)
                .seat(seat)
                .seatGrade(seatGrade)
                .build();
    }

    public static Seat createSeat(String rowNum, int columnNum, Stadium stadium) {
        return Seat.builder()
                .rowNum(rowNum)
                .columnNum(columnNum)
                .stadium(stadium)
                .build();
    }

    public static SeatGrade createSeatGrade(String name, int price, Musical musical) {
        return SeatGrade.builder()
                .name(name)
                .price(price)
                .musical(musical)
                .build();
    }

    public static ScheduleSeat createScheduleSeat(boolean isReserved, Seat seat, SeatGrade seatGrade, Schedule schedule) {
        return ScheduleSeat.builder()
                .isReserved(isReserved)
                .seat(seat)
                .seatGrade(seatGrade)
                .schedule(schedule)
                .build();
    }

    public static Schedule createSchedule(LocalDateTime startTime, LocalDateTime endTime, Musical musical) {
        return Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .musical(musical)
                .build();

    }
}
