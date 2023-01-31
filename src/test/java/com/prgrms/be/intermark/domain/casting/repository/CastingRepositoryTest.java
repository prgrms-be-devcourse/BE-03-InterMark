package com.prgrms.be.intermark.domain.casting.repository;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CastingRepositoryTest {

    @Autowired
    private CastingRepository castingRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private ActorRepository actorRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final Actor actor = ActorProvider.createActor();
    private final Casting casting = CastingProvider.createCasting(actor, musical);

    @BeforeEach
    void setUp() {
        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
        actorRepository.save(actor);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Success - 정상적인 캐스팅 값이 입력되면 저장에 성공한다")
        void saveSuccess() {
            // given & when
            Casting savedCasting = castingRepository.save(casting);
            Casting findCasting = castingRepository.findById(savedCasting.getId()).get();

            // then
            assertThat(findCasting).isEqualTo(savedCasting);
        }

        @Test
        @DisplayName("Fail - 연관된 배우 값이 없으면 저장에 실패한다")
        void saveFailByNoActor() {
            // given
            Casting casting = CastingProvider.createCasting(null, musical);

            // when & then
            assertThatThrownBy(() -> castingRepository.save(casting))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Fail - 연관된 뮤지컬 값이 없으면 저장에 실패한다")
        void saveFailByNoMusical() {
            // given
            Casting casting = CastingProvider.createCasting(actor, null);

            // when & then
            assertThatThrownBy(() -> castingRepository.save(casting))
                    .isExactlyInstanceOf(ConstraintViolationException.class);
        }
    }
}