package com.prgrms.be.intermark.domain.musical.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.service.ActorService;
import com.prgrms.be.intermark.domain.casting.service.CastingService;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCommandResponseDto;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDto;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalThumbnail;
import com.prgrms.be.intermark.domain.musical_seat.service.MusicalSeatService;
import com.prgrms.be.intermark.domain.seatgrade.service.SeatGradeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.service.StadiumService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalFacadeService {

	private final MusicalService musicalService;
	private final StadiumService stadiumService;
	private final UserService userService;
	private final SeatGradeService seatGradeService;
	private final MusicalThumbnailUploadService musicalThumbnailUploadService;
	private final MusicalDetailImageUploadService musicalDetailImageUploadService;
	private final MusicalSeatService musicalSeatService;
	private final ActorService actorService;
	private final CastingService castingService;

	@Transactional
	public MusicalCommandResponseDto create(
		MusicalCreateRequestDto createRequestDto,
		MultipartFile thumbnail,
		List<MultipartFile> detailImages
	) {
		Musical musical = createRequestDto.toEntity();
		MusicalThumbnail musicalThumbnail = musicalThumbnailUploadService.uploadThumbnail(thumbnail);
		Stadium findStadium = stadiumService.findById(createRequestDto.stadiumId());
		User findManager = userService.findById(createRequestDto.managerId());
		Musical createdMusical = musicalService.saveMusical(musical, musicalThumbnail, findStadium, findManager);

		musicalDetailImageUploadService.uploadFiles(detailImages, musical);
		seatGradeService.saveSeatGrade(createRequestDto.seatGrades(), createdMusical);
		musicalSeatService.saveMusicalSeat(createRequestDto.seats(), createdMusical);
		List<Actor> actors = actorService.findActors(createRequestDto.actors());
		castingService.saveCasting(actors, createdMusical);

		return MusicalCommandResponseDto.from(createdMusical);
	}
}
