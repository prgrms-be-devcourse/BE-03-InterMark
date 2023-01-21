package com.prgrms.be.intermark.domain.stadium.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StadiumService {

	private final StadiumRepository stadiumRepository;

	@Transactional(readOnly = true)
	public Stadium findById(Long id) {
		return stadiumRepository.findById(id)
			.orElseThrow(() -> {
				throw new EntityNotFoundException("존재하지 않는 공연장입니다");
			});
	}
}
