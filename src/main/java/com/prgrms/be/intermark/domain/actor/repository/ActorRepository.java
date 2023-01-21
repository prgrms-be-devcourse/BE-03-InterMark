package com.prgrms.be.intermark.domain.actor.repository;

import com.prgrms.be.intermark.domain.actor.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

	Optional<Actor> findActorByName(String name);
}
