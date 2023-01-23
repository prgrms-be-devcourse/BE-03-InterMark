package com.prgrms.be.intermark.domain.musical.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.common.dto.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.common.service.UploadImageServiceImpl;
import com.prgrms.be.intermark.domain.casting.service.CastingService;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCommandResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
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
	private final UploadImageServiceImpl uploadImageServiceImpl;
	private final MusicalDetailImageService musicalDetailImageService;
	private final MusicalSeatService musicalSeatService;
	private final CastingService castingService;

	@Transactional
	public MusicalCommandResponseDTO create(
		MusicalCreateRequestDTO createRequestDto,
		MultipartFile thumbnail,
		List<MultipartFile> detailImages
	) {
		Musical createdMusical = createRequestDto.toEntity();

		ImageResponseDTO thumbnailInfo = uploadImageServiceImpl.uploadImage(thumbnail);
		Stadium stadium = stadiumService.findById(createRequestDto.stadiumId());
		User manager = userService.findById(createRequestDto.managerId());
		Musical savedMusical = musicalService.save(createdMusical, thumbnailInfo.path(), stadium, manager);

		List<ImageResponseDTO> detailImagesInfo = uploadImageServiceImpl.uploadImages(detailImages);
		musicalDetailImageService.save(detailImagesInfo, savedMusical);
		seatGradeService.save(createRequestDto.seatGrades(), savedMusical);
		musicalSeatService.save(createRequestDto.seats(), savedMusical);
		castingService.save(createRequestDto.actorIds(), savedMusical);

		return MusicalCommandResponseDTO.from(savedMusical);
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
}
