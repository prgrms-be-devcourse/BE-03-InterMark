package com.prgrms.be.intermark.domain.performance.service;

import com.prgrms.be.intermark.common.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.performance.Performance;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceInfoResponseDTO;
import com.prgrms.be.intermark.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerformanceAdminService {

    private final PerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<Performance, PerformanceInfoResponseDTO> findAllPerformances(Pageable pageable) {

        Page<Performance> performancePage = performanceRepository.findAll(pageable);

        return new PageResponseDTO<>(performancePage, PerformanceInfoResponseDTO::from, PageListIndexSize.ADMIN_PERFORMANCE_LIST_INDEX_SIZE);
    }
}
