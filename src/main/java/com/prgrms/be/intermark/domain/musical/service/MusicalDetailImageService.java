package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical.repository.MusicalDetailImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicalDetailImageService {

    private final MusicalDetailImageRepository musicalDetailImageRepository;

    public void save(List<ImageResponseDTO> imageResponseDTOs, Musical musical) {
        imageResponseDTOs.forEach(
                imageResponse -> {
                    MusicalDetailImage detailImage = MusicalDetailImage.builder()
                            .musical(musical)
                            .originalFileName(imageResponse.originalFileName())
                            .imageUrl(imageResponse.path())
                            .build();

                    musicalDetailImageRepository.save(detailImage);
                }
        );
    }

    public void update(List<ImageResponseDTO> imageResponseDTOs, Musical musical) {
        musicalDetailImageRepository.deleteByMusical(musical);

        imageResponseDTOs.forEach(
                imageResponse -> {
                    MusicalDetailImage detailImage = MusicalDetailImage.builder()
                            .musical(musical)
                            .originalFileName(imageResponse.originalFileName())
                            .imageUrl(imageResponse.path())
                            .build();

                    musicalDetailImageRepository.save(detailImage);
                }
        );
    }
}
