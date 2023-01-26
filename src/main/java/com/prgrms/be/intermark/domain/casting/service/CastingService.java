package com.prgrms.be.intermark.domain.casting.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.casting.repository.CastingRepository;
import com.prgrms.be.intermark.domain.musical.model.Musical;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CastingService {

    private final CastingRepository castingRepository;
    private final ActorRepository actorRepository;

    @Transactional
    public void save(List<Long> actorIds, Musical musical) {
        actorIds
                .forEach(actorId -> {
                    Actor actor = actorRepository.findById(actorId)
                            .orElseThrow(() -> {
                                throw new EntityNotFoundException("존재하지 않는 배우입니다");
                            });

                    Casting casting = Casting.builder()
                            .actor(actor)
                            .musical(musical)
                            .build();
                    castingRepository.save(casting);
                });
    }

    public void update(List<Long> actorIds, Musical musical) {

        castingRepository.deleteByMusical(musical);

        actorIds
                .forEach(actorId -> {
                    Actor actor = actorRepository.findById(actorId)
                            .orElseThrow(() -> {
                                throw new EntityNotFoundException("존재하지 않는 배우입니다");
                            });

                    Casting casting = Casting.builder()
                            .actor(actor)
                            .musical(musical)
                            .build();
                    castingRepository.save(casting);
                });
    }
}
