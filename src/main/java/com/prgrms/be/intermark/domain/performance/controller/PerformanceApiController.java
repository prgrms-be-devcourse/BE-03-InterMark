package com.prgrms.be.intermark.domain.performance.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prgrms.be.intermark.domain.performance.dto.PerformanceCommandResponseDto;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCreateRequestDto;
import com.prgrms.be.intermark.domain.performance.service.PerformanceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceApiController {

	private final PerformanceService performanceService;

	@PostMapping
	public ResponseEntity<PerformanceCommandResponseDto> create(
		@RequestBody @Valid PerformanceCreateRequestDto createRequestDto
	) {
		PerformanceCommandResponseDto commandResponseDto = performanceService.create(createRequestDto);
		URI location = URI.create("/api/v1/performances/" + commandResponseDto.performanceId());
		return ResponseEntity.created(location).build();
	}
}
