package com.prgrms.be.intermark.domain.seatgrade.service;

import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatGradeService {

	private final SeatGradeRepository seatGradeRepository;

	@Transactional
	public void save(List<MusicalSeatGradeCreateRequestDTO> createRequestDTOs, Musical musical) {
		createRequestDTOs
				.forEach(seatGrade -> {
					SeatGrade createdSeatGrade = seatGrade.toEntity();
					createdSeatGrade.setMusical(musical);
					seatGradeRepository.save(createdSeatGrade);
				});
	}

	public void update(List<MusicalSeatGradeUpdateRequestDTO> musicalSeatGradeUpdateRequestDTOs, Musical musical) {
		seatGradeRepository.deleteByMusical(musical);
		seatGradeRepository.flush();

		musicalSeatGradeUpdateRequestDTOs
				.forEach(seatGrade -> {
					SeatGrade createdSeatGrade = seatGrade.toEntity();
					createdSeatGrade.setMusical(musical);
					seatGradeRepository.save(createdSeatGrade);
				});
	}
}
