package com.prgrms.be.intermark.domain.casting.repository;

import com.prgrms.be.intermark.domain.casting.model.Casting;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastingRepository extends JpaRepository<Casting, Long> {

    void deleteByMusical(Musical musical);
}
