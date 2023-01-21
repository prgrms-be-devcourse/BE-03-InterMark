package com.prgrms.be.intermark.domain.musical.service;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCommandResponseDto;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDto;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.musical_seat.model.MusicalSeat;
import com.prgrms.be.intermark.domain.musical_seat.repository.MusicalSeatRepository;
import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.seatgrade.model.SeatGrade;
import com.prgrms.be.intermark.domain.seatgrade.repository.SeatGradeRepository;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalService {

	private final MusicalRepository musicalRepository;
	private final StadiumRepository stadiumRepository;
	private final SeatGradeRepository seatGradeRepository;
	private final SeatRepository seatRepository;
	private final MusicalSeatRepository musicalSeatRepository;
	private final CastingRepository castingRepository;
	private final ActorRepository actorRepository;
	private final UserRepository userRepository;
	private final FIleUploadService fIleUploadService;

	@Transactional
	public MusicalCommandResponseDto create(
		MusicalCreateRequestDto createRequestDto,
		MultipartFile thumbnail,
		List<MultipartFile> detailImages
	) throws IOException {

		Musical musical = createRequestDto.toEntity();
		stadiumRepository.findById(createRequestDto.stadiumId())
			.ifPresentOrElse(
				stadium -> musical.setStadium(stadium)
				, () -> {
					throw new EntityNotFoundException("존재하지 않는 공연장입니다");
				});
		userRepository.findById(createRequestDto.managerId())
			.ifPresentOrElse(
			user -> musical.setUser(user)
			, () -> {
				throw new EntityNotFoundException("존재하지 않는 관리자입니다");
			});

		Musical savedMusical = musicalRepository.save(musical);

		createRequestDto.seatGrades()
			.forEach(seatGrade -> {
				SeatGrade createdSeatGrade = seatGrade.toEntity();
				createdSeatGrade.setMusical(musical);
				seatGradeRepository.save(createdSeatGrade);
			});
		createRequestDto.actors()
			.forEach(actorId -> {
				actorRepository.findById(actorId)
					.ifPresentOrElse(
						actor -> {
							Casting casting = Casting.builder()
								.actor(actor)
								.musical(musical)
								.build();
							castingRepository.save(casting);
						},
						() -> {
							throw new EntityNotFoundException("존재하지 않는 배우입니다");
						}
					);
			});
		createRequestDto.seats()
			.forEach(musicalSeat -> {
				Seat seat = seatRepository.findById(musicalSeat.seatId())
					.orElseThrow(() -> {
						throw new EntityNotFoundException("존재하지 않는 좌석입니다");
					});
				SeatGrade seatGrade = seatGradeRepository.findSeatGradeByNameAndMusical(musicalSeat.seatGradeName(), musical)
					.orElseThrow(() -> {
						throw new EntityNotFoundException("존재하지 않는 좌석 등급입니다");
					});
				MusicalSeat createdMusicalSeat = MusicalSeat.builder()
					.seat(seat)
					.musical(musical)
					.seatGrade(seatGrade)
					.build();
				musicalSeatRepository.save(createdMusicalSeat);
			});

		String thumbnailUrl = fIleUploadService.uploadFile(thumbnail, musical);
		musical.setThumbnailUrl(thumbnailUrl);
		fIleUploadService.uploadFiles(detailImages, musical);

		return MusicalCommandResponseDto.from(savedMusical);
	}

}
