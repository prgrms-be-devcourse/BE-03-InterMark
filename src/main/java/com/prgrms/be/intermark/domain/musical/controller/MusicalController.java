package com.prgrms.be.intermark.domain.musical.controller;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.*;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/musicals")
public class MusicalController {

	private final MusicalFacadeService musicalFacadeService;

	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> createMusical(
		@RequestPart @Valid MusicalCreateRequestDTO createRequestDto,
		@RequestPart(required = false) MultipartFile thumbnail,
		@RequestPart(required = false) List<MultipartFile> detailImages
	) {
		MusicalCommandResponseDTO responseDto = musicalFacadeService.create(createRequestDto, thumbnail, detailImages);
		URI location = URI.create("/api/v1/musicals/" + responseDto.id());
		return ResponseEntity.created(location).build();
	}

	@GetMapping
	public ResponseEntity<PageResponseDTO<Musical, MusicalSummaryResponseDTO>> getAllMusicals(Pageable pageable) {
		PageResponseDTO<Musical, MusicalSummaryResponseDTO> musicals = musicalFacadeService.findAllMusicals(pageable);

		return ResponseEntity.ok(musicals);
	}

	@GetMapping("/{musicalId}")
	public ResponseEntity<MusicalDetailResponseDTO> getMusical(@PathVariable Long musicalId) {
		MusicalDetailResponseDTO musical = musicalFacadeService.findMusicalById(musicalId);

		return ResponseEntity.ok(musical);
	}

	@PutMapping(value = "/{musicalId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> updateMusical(
			@PathVariable Long musicalId,
			@RequestPart @Valid MusicalUpdateRequestDTO musicalSeatUpdateRequestDTO,
			@RequestPart MultipartFile thumbnail,
			@RequestPart List<MultipartFile> detailImages
	) {
		musicalFacadeService.update(musicalId, musicalSeatUpdateRequestDTO, thumbnail, detailImages);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{musicalId}")
	public ResponseEntity<Void> deleteMusical(@PathVariable Long musicalId) {
		musicalFacadeService.deleteMusical(musicalId);

		return ResponseEntity.noContent().build();
	}
}
