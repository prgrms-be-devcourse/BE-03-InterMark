package com.prgrms.be.intermark.domain.musical_detail_image.repository;

import com.prgrms.be.intermark.domain.musical_detail_image.model.MusicalDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicalDetailImageRepository extends JpaRepository<MusicalDetailImage, Long> {

	Optional<MusicalDetailImage> findByImageUrl(String url);
}
