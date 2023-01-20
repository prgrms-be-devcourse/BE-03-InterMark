package com.prgrms.be.intermark.domain.musical.controller;

import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.service.MusicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/musicals")
@RequiredArgsConstructor
public class MusicalController {

    private final MusicalService musicalService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<Musical, MusicalResponseDTO>> getAllMusicals(Pageable pageable) {
        PageResponseDTO<Musical, MusicalResponseDTO> musicals = musicalService.findAllMusicals(pageable);

        return ResponseEntity.ok(musicals);
    }

    @GetMapping("/{musicalId}")
    public ResponseEntity<MusicalDetailResponseDTO> getMusical(@PathVariable Long musicalId) {
        MusicalDetailResponseDTO musical = musicalService.findMusicalById(musicalId);

        return ResponseEntity.ok(musical);
    }
}
