package com.prgrms.be.intermark.domain.performance.repository;

import com.prgrms.be.intermark.domain.performance.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
