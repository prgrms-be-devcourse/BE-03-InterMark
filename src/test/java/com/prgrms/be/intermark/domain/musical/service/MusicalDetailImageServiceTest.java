package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.domain.musical.model.Genre;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.model.ViewRating;
import com.prgrms.be.intermark.domain.musical.repository.MusicalDetailImageRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicalDetailImageServiceTest {

    @InjectMocks
    private MusicalDetailImageService musicalDetailImageService;

    @Mock
    private MusicalDetailImageRepository musicalDetailImageRepository;


    String thumbnailUrl = "abcedf";
    Stadium stadium = Stadium.builder()
            .name("예술의 전당")
            .address("서울특별시")
            .imageUrl("abcefg")
            .build();
    User user = User.builder()
            .birth(LocalDate.of(1997, 10, 10))
            .social(Social.GOOGLE)
            .socialId("intermark")
            .refreshToken("abcdefg")
            .nickname("인터마크 관리자")
            .role(UserRole.ADMIN)
            .isDeleted(false)
            .email("intermark@gmail.com")
            .build();
    Musical musical = Musical.builder()
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

    @Test
    @DisplayName("성공 - 정상 뮤지컬 상세 이미지 값이 들어오면 저장에 성공한다 - save")
    void save() {
        // given
        MusicalDetailImage musicalDetailImage1 = MusicalDetailImage.builder()
                .originalFileName("업로드 파일1")
                .imageUrl("a")
                .musical(musical)
                .build();
        MusicalDetailImage musicalDetailImage2 = MusicalDetailImage.builder()
                .originalFileName("업로드 파일2")
                .imageUrl("ab")
                .musical(musical)
                .build();
        List<MusicalDetailImage> musicalDetailImages = List.of(musicalDetailImage1, musicalDetailImage2);
        when(musicalDetailImageRepository.save(any(MusicalDetailImage.class))).thenReturn(any(MusicalDetailImage.class));

        // when
        musicalDetailImageService.save(musicalDetailImages);

        // then
        verify(musicalDetailImageRepository, times(musicalDetailImages.size())).save(any(MusicalDetailImage.class));
    }
}