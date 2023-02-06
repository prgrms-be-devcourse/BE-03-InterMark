package com.prgrms.be.intermark.domain.musical.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalCreateRequestDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.service.MusicalFacadeService;

import lombok.RequiredArgsConstructor;

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
		Long musicalId = musicalFacadeService.create(createRequestDto, thumbnail, detailImages);
		URI location = URI.create("/api/v1/musicals/" + musicalId);
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
			@RequestPart @Valid MusicalUpdateRequestDTO musicalUpdateRequestDTO,
			@RequestPart MultipartFile thumbnail,
			@RequestPart List<MultipartFile> detailImages
	) {
		musicalFacadeService.update(musicalId, musicalUpdateRequestDTO, thumbnail, detailImages);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{musicalId}")
	public ResponseEntity<Void> deleteMusical(@PathVariable Long musicalId) {
		musicalFacadeService.deleteMusical(musicalId);

		return ResponseEntity.noContent().build();
	}
}
