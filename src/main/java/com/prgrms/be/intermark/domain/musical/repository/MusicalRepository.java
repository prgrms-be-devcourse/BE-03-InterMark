package com.prgrms.be.intermark.domain.musical.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MusicalRepository extends JpaRepository<Musical, Long> {

    @Query("SELECT m FROM Musical m LEFT JOIN FETCH m.stadium LEFT JOIN FETCH m.castings c LEFT JOIN FETCH c.actor WHERE m.id = :musicalId AND m.isDeleted = FALSE")
    Optional<Musical> findMusicalsFetchByMusicalId(@Param("musicalId") Long musicalId);
}
