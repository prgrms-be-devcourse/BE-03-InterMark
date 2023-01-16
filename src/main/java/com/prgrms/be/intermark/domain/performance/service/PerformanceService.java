package com.prgrms.be.intermark.domain.performance.service;

import org.springframework.stereotype.Service;

import com.prgrms.be.intermark.domain.performance.dto.PerformanceCommandResponseDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCreateRequestDTO;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;

	public PerformanceCommandResponseDTO create(PerformanceCreateRequestDTO createRequestDTO) {
		Performance performance = createRequestDTO.toEntity();
		Performance savedPerformance = performanceRepository.save(performance);
		return new PerformanceCommandResponseDTO(savedPerformance.getId());
	}
}
