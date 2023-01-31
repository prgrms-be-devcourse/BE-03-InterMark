package com.prgrms.be.intermark.domain.seatgrade.service;

import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatGradeUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatGradeService {

	private final SeatGradeRepository seatGradeRepository;

	@Transactional
	public void save(List<SeatGrade> seatGrades) {
		seatGrades.forEach(seatGradeRepository::save);
	}

	@Transactional(readOnly = true)
	public SeatGrade findByNameAndMusical(String seatGradeName, Musical musical) {
		return seatGradeRepository.findByNameAndMusical(seatGradeName, musical)
				.orElseThrow(() -> {
					throw new EntityNotFoundException("존재하지 않는 좌석 등급입니다");
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

	@Transactional
	public void deleteAllByMusical(Musical musical) {
		seatGradeRepository.findByMusicalAndIsDeletedIsFalse(musical)
			.forEach(SeatGrade::deleteSeatGrade);
	}
}
