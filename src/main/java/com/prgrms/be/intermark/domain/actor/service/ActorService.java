package com.prgrms.be.intermark.domain.actor.service;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import com.prgrms.be.intermark.domain.actor.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    @Transactional(readOnly = true)
    public Actor findById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("존재하지 않는 배우입니다");
                });
    }
}
