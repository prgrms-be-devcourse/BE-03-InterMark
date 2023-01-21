package com.prgrms.be.intermark.domain.musical.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalThumbnail;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalService {

	private final MusicalRepository musicalRepository;

	@Transactional
	public Musical saveMusical(
		Musical musical,
		MusicalThumbnail thumbnail,
		Stadium stadium,
		User user
	) {
		musical.setThumbnail(thumbnail);
		musical.setStadium(stadium);
		musical.setUser(user);
		Musical savedMusical = musicalRepository.save(musical);

		return savedMusical;
	}

	@Transactional(readOnly = true)
	public Page<Musical> findAllMusicals(Pageable pageable) {

		return musicalRepository.findAll(pageable);
	}

}
