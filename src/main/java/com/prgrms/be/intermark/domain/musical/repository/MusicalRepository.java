package com.prgrms.be.intermark.domain.musical.repository;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
}
