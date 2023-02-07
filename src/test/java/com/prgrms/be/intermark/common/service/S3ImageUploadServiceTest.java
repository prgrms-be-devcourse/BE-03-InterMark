package com.prgrms.be.intermark.common.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@ExtendWith(MockitoExtension.class)
class S3ImageUploadServiceTest {

    private static final String UPLOAD_IMAGE_PATH = "src/test/resources/testMusicalThumbnail.jpg";

    @InjectMocks
    private S3ImageUploadService s3ImageUploadService;

    @Mock
    private AmazonS3 amazonS3;

    @Nested
    @DisplayName("uploadImage")
    class UploadImage {

        @Test
        @DisplayName("Success - S3 에 이미지 업로드를 성공한다.")
        void uploadImageSuccess() throws IOException {
            // given
            FileInputStream fileInputStream = new FileInputStream(UPLOAD_IMAGE_PATH);
            MockMultipartFile multipartFile = new MockMultipartFile("testImage", "testMusicalThumbnail.jpg", "jpg", fileInputStream);

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
            when(amazonS3.getUrl(any(), anyString())).thenReturn(new URL("https://s3.intermark"));

            // when
            s3ImageUploadService.uploadImage(multipartFile, "subpath/");

            // then
            verify(amazonS3).putObject(any(PutObjectRequest.class));
            verify(amazonS3).getUrl(any(), anyString());
        }

        @Test
        @DisplayName("Fail - 이미지가 없으면 S3 에 이미지 업로드를 실패한다.")
        void uploadImageFail() {
            // given
            MockMultipartFile multipartFile = new MockMultipartFile("testImage", "testMusicalThumbnail.jpg", "jpg", "".getBytes());

            // when & then
            Assertions.assertThatThrownBy(() -> s3ImageUploadService.uploadImage(multipartFile, "subpath/"))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

    }
}