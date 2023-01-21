package com.prgrms.be.intermark.domain.actor.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActorService {

	private final ActorRepository actorRepository;

	@Transactional(readOnly = true)
	public List<Actor> findActors(List<Long> actorIds) {
		return actorIds.stream()
			.map(actorId -> actorRepository.findById(actorId)
				.orElseThrow(() -> {
					throw new EntityNotFoundException("존재하지 않는 배우입니다");
				})).toList();
	}
}
