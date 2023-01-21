package com.prgrms.be.intermark.domain.casting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CastingService {

	private final CastingRepository castingRepository;

	@Transactional
	public void saveCasting(List<Actor> actors, Musical musical) {
		actors.forEach(actor -> {
			Casting casting = Casting.builder()
				.actor(actor)
				.musical(musical)
				.build();
			castingRepository.save(casting);
		});
	}
}
