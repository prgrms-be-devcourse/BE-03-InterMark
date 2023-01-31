package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicalServiceTest {

    @InjectMocks
    private MusicalService musicalService;

    @Mock
    private MusicalRepository musicalRepository;

    @Mock
    private Musical savedMusical;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);


    @Test
    @DisplayName("Success - 올바른 값이 들어오면 뮤지컬을 저장한다 - save")
    void saveSuccess() {
        // given
        when(musicalRepository.save(musical)).thenReturn(any(Musical.class));

        // when
        musicalService.save(musical);

        // then
        verify(musicalRepository).save(musical);
    }

    @Nested
    @DisplayName("deleteMusical")
    class DeleteMusical {

        @Test
        @DisplayName("Success - 삭제할 뮤지컬이 있으면 삭제에 성공한다.")
        void deleteMusicalSuccess() {
            // given
            when(savedMusical.isDeleted()).thenReturn(false);

            // when
            musicalService.deleteMusical(savedMusical);

            // then
            verify(savedMusical).isDeleted();
            verify(savedMusical).deleteMusical();
        }

        @Test()
        @DisplayName("Fail - 이미 삭제된 뮤지컬이면 삭제에 실패한다.")
        void deleteMusicalFail() {
            // given
            when(savedMusical.isDeleted()).thenReturn(true);

            // then
            assertThatThrownBy(() -> musicalService.deleteMusical(savedMusical))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(savedMusical).isDeleted();
            verify(savedMusical, never()).deleteMusical();
        }
    }
}