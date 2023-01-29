package com.prgrms.be.intermark.domain.musical.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import com.prgrms.be.intermark.domain.util.MusicalDetailImageProvider;
import com.prgrms.be.intermark.domain.util.MusicalProvider;
import com.prgrms.be.intermark.domain.util.StadiumProvider;
import com.prgrms.be.intermark.domain.util.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MusicalDetailImageRepositoryTest {

    @Autowired
    private MusicalDetailImageRepository musicalDetailImageRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    private final String thumbnailUrl = "https://intermark.com";
    private final Stadium stadium = StadiumProvider.createStadium();
    private final User user = UserProvider.createUser();
    private final Musical musical = MusicalProvider.createMusical(thumbnailUrl, stadium, user);
    private final MusicalDetailImage musicalDetailImage = MusicalDetailImageProvider.createMusicalDetailImage(musical);

    @BeforeEach
    void setUp() {
        stadiumRepository.save(stadium);
        userRepository.save(user);
        musicalRepository.save(musical);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("성공 - 정상 뮤지컬 상세 이미지 값이 들어오면 저장에 성공한다")
        void saveSuccess() {
            // given & when
            MusicalDetailImage savedMusicalDetailImage = musicalDetailImageRepository.save(musicalDetailImage);

            // then
            assertThat(savedMusicalDetailImage.getId()).isNotNull();
            assertThat(savedMusicalDetailImage)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(musicalDetailImage);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("실패 - 이미지 원래 이름 값이 null, 빈 값, 공백이면 저장에 실패한다")
        void saveFailByWrongOriginalFileName(String wrongOriginalFileName) {
            // given
            MusicalDetailImage wrongMusicalDetailImage = MusicalDetailImage.builder()
                    .originalFileName(wrongOriginalFileName)
                    .imageUrl("a")
                    .musical(musical)
                    .build();

            // when & then
            assertThatThrownBy(() -> musicalDetailImageRepository.save(wrongMusicalDetailImage))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("실패 - 이미지 Url 값이 null, 빈 값, 공백이면 저장에 실패한다")
        void saveFailByWrongImageUrl(String wrongImageUrl) {
            // given
            MusicalDetailImage wrongMusicalDetailImage = MusicalDetailImage.builder()
                    .originalFileName("업로드 이미지")
                    .imageUrl(wrongImageUrl)
                    .musical(musical)
                    .build();

            // when & then
            assertThatThrownBy(() -> musicalDetailImageRepository.save(wrongMusicalDetailImage))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("실패 - 연관된 뮤지컬 값이 없으면 저장에 실패한다")
        void saveFailByNoMusical() {
            // given
            MusicalDetailImage wrongMusicalDetailImage = MusicalDetailImage.builder()
                    .originalFileName("업로드 이미지")
                    .imageUrl("a")
                    .build();

            // when & then
            assertThatThrownBy(() -> musicalDetailImageRepository.save(wrongMusicalDetailImage))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }
}