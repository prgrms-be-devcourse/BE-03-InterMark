package com.prgrms.be.intermark.domain.performance.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prgrms.be.intermark.common.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCommandResponseDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCreateRequestDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceInfoResponseDTO;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.service.PerformanceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceApiController {

	private final PerformanceService performanceService;

	@PostMapping
	public ResponseEntity<PerformanceCommandResponseDTO> create(
		@RequestBody @Valid PerformanceCreateRequestDTO createRequestDTO
	) {
		PerformanceCommandResponseDTO commandResponseDTO = performanceService.create(createRequestDTO);
		URI location = URI.create("/api/v1/performances/" + commandResponseDTO.performanceId());
		return ResponseEntity.created(location).body(commandResponseDTO);
	}

	@GetMapping
	public ResponseEntity<PageResponseDTO<Performance, PerformanceInfoResponseDTO>> getPerformances(Pageable pageable) {

		PageResponseDTO<Performance, PerformanceInfoResponseDTO> performancesPageResponseDTO = performanceService.findAllPerformances(pageable);

		return ResponseEntity.ok(
			performancesPageResponseDTO
		);
	}
}
