package com.prgrms.be.intermark.domain.musical_image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.musical_image.model.MusicalDetailImage;

public interface MusicalDetailImageRepository extends JpaRepository<MusicalDetailImage, Long> {

	Optional<MusicalDetailImage> findByImageUrl(String url);
}
