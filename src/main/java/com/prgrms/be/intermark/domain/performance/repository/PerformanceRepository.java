package com.prgrms.be.intermark.domain.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.performance.model.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
