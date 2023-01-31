package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MusicalProvider {

    public static Musical createMusical(String thumbnailUrl, Stadium stadium, User user) {
        return Musical.builder()
                .title("제목")
                .viewRating(ViewRating.ADULT)
                .genre(Genre.DRAMA)
                .description("설명")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 2, 1))
                .runningTime(60)
                .thumbnailUrl(thumbnailUrl)
                .stadium(stadium)
                .user(user)
                .build();
    }
}
