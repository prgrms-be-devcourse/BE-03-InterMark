package com.prgrms.be.intermark.util;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;

import java.time.LocalDate;

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
}
