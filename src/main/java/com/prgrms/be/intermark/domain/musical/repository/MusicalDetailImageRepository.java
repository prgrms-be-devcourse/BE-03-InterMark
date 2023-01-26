package com.prgrms.be.intermark.domain.musical.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;

public interface MusicalDetailImageRepository extends JpaRepository<MusicalDetailImage, Long> {

    Optional<MusicalDetailImage> findByImageUrl(String url);

    void deleteByMusical(Musical musical);
}
