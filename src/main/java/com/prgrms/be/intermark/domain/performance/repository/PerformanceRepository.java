package com.prgrms.be.intermark.domain.performance.repository;

import com.prgrms.be.intermark.domain.performance.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("select p from Performance p left join fetch p.performanceStadiums ps left join fetch ps.stadium where p.id = :performanceId")
    Optional<Performance> findPerformanceFetchById(@Param("performanceId") Long performanceId);
}
