package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.UploadImageServiceImpl;
import com.prgrms.be.intermark.domain.casting.service.CastingService;
import com.prgrms.be.intermark.domain.musical.dto.*;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_seat.service.MusicalSeatService;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.seatgrade.service.SeatGradeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.service.StadiumService;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicalFacadeService {

	private final MusicalService musicalService;
	private final StadiumService stadiumService;
	private final UserService userService;
	private final SeatGradeService seatGradeService;
	private final UploadImageServiceImpl uploadImageServiceImpl;
	private final MusicalDetailImageService musicalDetailImageService;
	private final MusicalSeatService musicalSeatService;
	private final CastingService castingService;
	private final TicketService ticketService;
	private final ScheduleService scheduleService;

	@Transactional
	public MusicalCommandResponseDTO create(
			MusicalCreateRequestDTO createRequestDto,
			MultipartFile thumbnail,
			List<MultipartFile> detailImages
	) {
		Musical createdMusical = createRequestDto.toEntity();

		ImageResponseDTO thumbnailInfo = uploadImageServiceImpl.uploadImage(thumbnail);
		Stadium stadium = stadiumService.findById(createRequestDto.stadiumId());
		User manager = userService.findByIdForFasade(createRequestDto.managerId());
		Musical savedMusical = musicalService.save(createdMusical, thumbnailInfo.path(), stadium, manager);

		List<ImageResponseDTO> detailImagesInfo = uploadImageServiceImpl.uploadImages(detailImages);
		musicalDetailImageService.save(detailImagesInfo, savedMusical);
		seatGradeService.save(createRequestDto.seatGrades(), savedMusical);
		musicalSeatService.save(createRequestDto.seats(), savedMusical);
		castingService.save(createRequestDto.actorIds(), savedMusical);

		return MusicalCommandResponseDTO.from(savedMusical);
	}

	@Transactional
	public void update(
			Long musicalId,
			MusicalUpdateRequestDTO musicalSeatUpdateRequestDTO,
			MultipartFile thumbnailImage,
			List<MultipartFile> detailImages
	) {
		Musical musical = musicalService.findMusicalById(musicalId);

		if (scheduleService.existsByMusical(musical)) {
			throw new IllegalArgumentException("이미 뮤지컬의 스케줄이 존재합니다.");
		}

		if (ticketService.existsByMusical(musical)) {
			throw new IllegalArgumentException("이미 예약된 뮤지컬입니다.");
		}

		ImageResponseDTO thumbnailInfo = uploadImageServiceImpl.uploadImage(thumbnailImage);
		Stadium stadium = stadiumService.findById(musicalSeatUpdateRequestDTO.stadiumId());
		User manager = userService.findByIdForFasade(musicalSeatUpdateRequestDTO.managerId());

		seatGradeService.update(musicalSeatUpdateRequestDTO.seatGrades(), musical);
		musicalSeatService.update(musicalSeatUpdateRequestDTO.seats(), musicalSeatUpdateRequestDTO.stadiumId(), musical);
		castingService.update(musicalSeatUpdateRequestDTO.actors(), musical);

		List<ImageResponseDTO> detailImagesInfo = uploadImageServiceImpl.uploadImages(detailImages);
		musicalDetailImageService.update(detailImagesInfo, musical);
		musicalService.updateMusical(musical, musicalSeatUpdateRequestDTO, thumbnailInfo.path(), stadium, manager);
	}

	@Transactional(readOnly = true)
	public PageResponseDTO<Musical, MusicalSummaryResponseDTO> findAllMusicals(Pageable pageable) {
		Page<Musical> musicalPage = musicalService.findAllMusicals(pageable);
		return new PageResponseDTO<>(musicalPage, MusicalSummaryResponseDTO::from, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);
	}

	@Transactional(readOnly = true)
	public MusicalDetailResponseDTO findMusicalById(Long musicalId) {
		Musical musical = musicalService.findMusicalById(musicalId);
		return MusicalDetailResponseDTO.from(musical);
	}

	@Transactional
	public void deleteMusical(Long musicalId) {
		Musical musical = musicalService.findMusicalById(musicalId);

		boolean hasReservedTicket = musical.getTickets()
			.stream()
			.anyMatch(Ticket::isReserved);

		if (hasReservedTicket) {
			throw new RuntimeException("예매된 티켓이 있어 뮤지컬을 삭제할 수 없습니다");
		}

		musicalService.deleteMusical(musical);
		castingService.deleteAllByMusical(musical);
		musicalDetailImageService.deleteAllByMusical(musical);
		scheduleService.deleteAllByMusical(musical);
		seatGradeService.deleteAllByMusical(musical);
		musicalSeatService.deleteAllByMusical(musical);
	}
}
