package com.prgrms.be.intermark.domain.performance_detail_image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.performance_detail_image.model.PerformanceDetailImage;

public interface PerformanceDetailImageRepository extends JpaRepository<PerformanceDetailImage, Long> {

	Optional<PerformanceDetailImage> findByImageUrl(String url);
}
