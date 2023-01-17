package com.prgrms.be.intermark.domain.performance_stadium.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;

public interface PerformanceStadiumRepository extends JpaRepository<PerformanceStadium, Long> {

	@Query("select ps from PerformanceStadium ps where ps.performance.id = :performanceId and  ps.stadium.id = :stadiumId")
	Optional<PerformanceStadium> findByPerformanceIdAndStadiumId(@Param("performanceId") Long performanceId, @Param("stadiumId") Long stadiumId);
}
