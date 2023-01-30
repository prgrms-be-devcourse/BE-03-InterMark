package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.repository.MusicalDetailImageRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.util.MusicalDetailImageProvider;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicalDetailImageServiceTest {

    @InjectMocks
    private MusicalDetailImageService musicalDetailImageService;

    @Mock
    private MusicalDetailImageRepository musicalDetailImageRepository;


    @Nested
    @DisplayName("save")
    class Save {

        private final String thumbnailUrl = "https://intermark.com";
        private final Stadium stadium = StadiumProvider.createStadium();
        private final User user = UserProvider.createUser();
        private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);

        @Test
        @DisplayName("성공 - 정상 뮤지컬 상세 이미지 값이 들어오면 저장에 성공한다")
        void saveSuccess() {
            // given
            MusicalDetailImage musicalDetailImage1 = MusicalDetailImageProvider.createMusicalDetailImage("업로드 파일1", "a", musical);
            MusicalDetailImage musicalDetailImage2 = MusicalDetailImageProvider.createMusicalDetailImage("업로드 파일2", "b", musical);
            List<MusicalDetailImage> musicalDetailImages = List.of(musicalDetailImage1, musicalDetailImage2);
            when(musicalDetailImageRepository.save(any(MusicalDetailImage.class))).thenReturn(any(MusicalDetailImage.class));

            // when
            musicalDetailImageService.save(musicalDetailImages);

            // then
            verify(musicalDetailImageRepository, times(musicalDetailImages.size())).save(any(MusicalDetailImage.class));
        }


        @Test
        @DisplayName("성공 - 해당 뮤지컬의 상세이미지를 전부 삭제한다.")
        void deleteAllByMusicalSuccess() {
            // given
            List<MusicalDetailImage> musicalDetailImages = List.of(mock(MusicalDetailImage.class), mock(MusicalDetailImage.class));
            when(musicalDetailImageRepository.findByMusicalAndIsDeletedIsFalse(musical)).thenReturn(musicalDetailImages);

            // when
            musicalDetailImageService.deleteAllByMusical(musical);

            // then
            verify(musicalDetailImageRepository).findByMusicalAndIsDeletedIsFalse(musical);
            for (MusicalDetailImage musicalDetailImage : musicalDetailImages) {
                verify(musicalDetailImage).deleteMusicalDetailImage();
            }
        }
    }
}