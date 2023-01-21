package com.prgrms.be.intermark.domain.musical.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.musical.model.MusicalThumbnail;

public interface MusicalThumbnailRepository extends JpaRepository<MusicalThumbnail, Long> {
}
