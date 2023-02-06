package com.prgrms.be.intermark.domain.musical_seat.service;

import com.prgrms.be.intermark.domain.musical.dto.MusicalSeatUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicalSeatService {

	private final SeatRepository seatRepository;
	private final SeatGradeRepository seatGradeRepository;
	private final MusicalSeatRepository musicalSeatRepository;

	@Transactional
	public void save(List<MusicalSeat> musicalSeats) {
		musicalSeats.forEach(musicalSeatRepository::save);
	}

	public void update(List<MusicalSeatUpdateRequestDTO> musicalSeatUpdateRequestDTOs, Long stadiumId, Musical musical) {
		musicalSeatRepository.deleteByMusical(musical);
		musicalSeatRepository.flush();

		musicalSeatUpdateRequestDTOs.forEach(musicalSeat -> {
			Seat seat = seatRepository.findByIdAndStadium(musicalSeat.seatId(), stadiumId)
					.orElseThrow(() -> {
						throw new EntityNotFoundException("존재하지 않는 좌석입니다.");
					});

			SeatGrade seatGrade = seatGradeRepository.findByNameAndMusical(musicalSeat.seatGradeName(), musical)
					.orElseThrow(() -> {
						throw new EntityNotFoundException("존재하지 않는 좌석 등급입니다.");
					});

			MusicalSeat createdMusicalSeat = MusicalSeat.builder()
					.seat(seat)
					.musical(musical)
					.seatGrade(seatGrade)
					.build();

			createdMusicalSeat.
					updateMusicalSeat(musical, seat, seatGrade);

			musicalSeatRepository.save(createdMusicalSeat);
		});
	}

	@Transactional
	public void deleteAllByMusical(Musical musical) {
		musicalSeatRepository.findByMusicalAndIsDeletedIsFalse(musical)
			.forEach(MusicalSeat::deleteMusicalSeat);
	}
}
