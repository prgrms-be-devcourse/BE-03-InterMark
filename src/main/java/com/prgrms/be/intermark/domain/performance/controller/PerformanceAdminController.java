package com.prgrms.be.intermark.domain.performance.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceInfoResponseDTO;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.service.PerformanceAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vi/admin/performances")
@RequiredArgsConstructor
public class PerformanceAdminController {

    private final PerformanceAdminService performanceAdminService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<Performance, PerformanceInfoResponseDTO>> getPerformances(Pageable pageable) {

        PageResponseDTO<Performance, PerformanceInfoResponseDTO> performancesPageResponseDTO = performanceAdminService.findAllPerformances(pageable);

        return ResponseEntity.ok(
                performancesPageResponseDTO
        );
    }
}
