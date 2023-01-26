package com.prgrms.be.intermark.domain.musical.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.musical.dto.MusicalUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.repository.MusicalRepository;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalService {

	private final MusicalRepository musicalRepository;

	@Transactional
	public Musical save(
		Musical musical,
		String thumbnailUrl,
		Stadium stadium,
		User user
	) {
		musical.setThumbnailUrl(thumbnailUrl);
		musical.setStadium(stadium);
		musical.setUser(user);
		return musicalRepository.save(musical);
	}

	@Transactional(readOnly = true)
	public Page<Musical> findAllMusicals(Pageable pageable) {

		return musicalRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Musical findMusicalById(Long musicalId) {

		return musicalRepository.findMusicalsFetchByMusicalId(musicalId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 뮤지컬입니다."));
	}

	@Transactional
	public void updateMusical(Musical musical,
							  MusicalUpdateRequestDTO musicalSeatUpdateRequestDTO,
							  String thumbnailInfo,
							  Stadium stadium,
							  User manager) {
		musical.updateMusical(
				musicalSeatUpdateRequestDTO.title(),
				thumbnailInfo,
				musicalSeatUpdateRequestDTO.viewRating(),
				musicalSeatUpdateRequestDTO.genre(),
				musicalSeatUpdateRequestDTO.description(),
				musicalSeatUpdateRequestDTO.startDate(),
				musicalSeatUpdateRequestDTO.endDate(),
				musicalSeatUpdateRequestDTO.runningTime(),
				stadium,
				manager
		);
	}

	@Transactional
	public void deleteMusical(Musical musical) {

		if (musical.isDeleted()) {
			throw new EntityNotFoundException("이미 삭제된 뮤지컬입니다");
		}

		musical.deleteMusical();
	}
}
