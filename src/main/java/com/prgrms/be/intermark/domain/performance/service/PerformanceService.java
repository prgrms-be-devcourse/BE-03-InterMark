package com.prgrms.be.intermark.domain.performance.service;

import org.springframework.stereotype.Service;

import com.prgrms.be.intermark.domain.performance.dto.PerformanceCommandResponseDto;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCreateRequestDto;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;

	public PerformanceCommandResponseDto create(PerformanceCreateRequestDto createRequestDto) {
		Performance performance = createRequestDto.toEntity();
		Performance savedPerformance = performanceRepository.save(performance);
		return new PerformanceCommandResponseDto(savedPerformance.getId());
	}
}
