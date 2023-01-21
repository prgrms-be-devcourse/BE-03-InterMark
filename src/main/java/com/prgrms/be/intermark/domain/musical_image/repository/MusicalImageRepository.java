package com.prgrms.be.intermark.domain.musical_image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.musical_image.model.MusicalImage;

public interface MusicalImageRepository extends JpaRepository<MusicalImage, Long> {

	Optional<MusicalImage> findByImageUrl(String url);
}
