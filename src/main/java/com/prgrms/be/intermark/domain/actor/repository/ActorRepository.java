package com.prgrms.be.intermark.domain.actor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.actor.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {

	Optional<Actor> findActorByName(String name);
}
