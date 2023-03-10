package com.prgrms.be.intermark.domain.casting.service;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.util.ActorProvider;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CastingServiceTest {

    @InjectMocks
    private CastingService castingService;

    @Mock
    private CastingRepository castingRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final Actor actor = ActorProvider.createActor();

    @Test
    @DisplayName("Success - 정상적인 캐스팅 값이 들어오면 저장에 성공한다 - save")
    void saveSuccess() {
        // given
        List<Casting> castings = List.of(mock(Casting.class), mock(Casting.class));
        when(castingRepository.save(any(Casting.class))).thenReturn(any(Casting.class));

        // when
        castingService.save(castings);

        // then
        verify(castingRepository, times(castings.size())).save(any(Casting.class));
    }

    @Test
    @DisplayName("Success - 해당 뮤지컬의 캐스팅을 전부 삭제한다. - deleteAllByMusical")
    void deleteAllByMusicalSuccess() {
        // given
        List<Casting> castings = List.of(mock(Casting.class), mock(Casting.class));
        when(castingRepository.findByMusicalAndIsDeletedIsFalse(musical)).thenReturn(castings);

        // when
        castingService.deleteAllByMusical(musical);

        // then
        verify(castingRepository).findByMusicalAndIsDeletedIsFalse(musical);
        for (Casting casting : castings) {
            verify(casting).deleteCasting();
        }
    }
}