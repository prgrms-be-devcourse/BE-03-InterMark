package com.prgrms.be.intermark.domain.casting.repository;

import com.prgrms.be.intermark.domain.casting.Casting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CastingRepository extends JpaRepository<Casting, Long> {

    @Query("select c from Casting c join fetch c.actor where c.performance.id = :performanceId")
    List<Casting> findAllFetchByPerformanceId(@Param(value = "performanceId") Long performanceId);
}
