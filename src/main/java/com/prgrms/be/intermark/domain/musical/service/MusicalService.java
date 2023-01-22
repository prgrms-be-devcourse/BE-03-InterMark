package com.prgrms.be.intermark.domain.musical.service;

import com.prgrms.be.intermark.common.dto.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalResponseDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class MusicalService {

    private final MusicalRepository musicalRepository;

    @Transactional(readOnly = true)
    public PageResponseDTO<Musical, MusicalResponseDTO> findAllMusicals(Pageable pageable) {

        Page<Musical> musicalPage = musicalRepository.findAll(pageable);

        return new PageResponseDTO<>(musicalPage, MusicalResponseDTO::from, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);
    }

    @Transactional(readOnly = true)
    public MusicalDetailResponseDTO findMusicalById(Long musicalId) {

        Musical musical = musicalRepository.findMusicalsFetchByMusicalId(musicalId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 뮤지컬입니다."));

        return MusicalDetailResponseDTO.from(musical);
    }
}
