package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.model.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActorProvider {

    public static Actor createActor() {
        return Actor.builder()
                .name("kwon")
                .birth(LocalDate.of(1997, 10, 10))
                .gender(Gender.MALE)
                .profileImageUrl("a")
                .build();
    }

    public static Actor createActor(String name, LocalDate birth, Gender gender, String profileImageUrl) {
        return Actor.builder()
                .name(name)
                .birth(birth)
                .gender(gender)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
