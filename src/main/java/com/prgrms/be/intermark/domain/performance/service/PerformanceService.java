package com.prgrms.be.intermark.domain.performance.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.Casting;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCommandResponseDTO;
import com.prgrms.be.intermark.domain.performance.dto.PerformanceCreateRequestDTO;
import com.prgrms.be.intermark.domain.performance.model.Performance;
import com.prgrms.be.intermark.domain.performance.repository.PerformanceRepository;
import com.prgrms.be.intermark.domain.performance_detail_image.model.PerformanceDetailImage;
import com.prgrms.be.intermark.domain.performance_detail_image.repository.PerformanceDetailImageRepository;
import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	private final StadiumRepository stadiumRepository;
	private final ActorRepository actorRepository;
	private final PerformanceDetailImageRepository performanceDetailImageRepository;

	@Transactional
	public PerformanceCommandResponseDTO create(PerformanceCreateRequestDTO createRequestDTO) {
		Performance performance = createRequestDTO.toEntity();
		Performance savedPerformance = performanceRepository.save(performance);

		stadiumRepository.findById(createRequestDTO.stadiumId())
			.ifPresentOrElse(
				stadium -> performance.addPerformanceStadium(new PerformanceStadium(performance, stadium)),
				() -> {
					throw new EntityNotFoundException("해당하는 공연장이 없습니다");
				});

		createRequestDTO.actorIds()
			.forEach(actorId -> {
				actorRepository.findById(actorId)
					.ifPresentOrElse(
						actor -> performance.addCasting(new Casting(actor, performance)),
						() -> {
							throw new EntityNotFoundException("해당하는 배우가 없습니다");
						});
			});

		createRequestDTO.detailImageUrls()
			.forEach(url -> {
				performanceDetailImageRepository.findByImageUrl(url)
					.ifPresentOrElse(
						image -> performance.addPerformanceDetailImage(new PerformanceDetailImage(performance, url)),
						() -> {
							PerformanceDetailImage savedPerformanceDetailImage =
								performanceDetailImageRepository.save(new PerformanceDetailImage(performance, url));
							performance.addPerformanceDetailImage(savedPerformanceDetailImage);
						});
			});

		return new PerformanceCommandResponseDTO(savedPerformance.getId());
	}

}
