package com.prgrms.be.intermark.domain.stadium.repository;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {
}
