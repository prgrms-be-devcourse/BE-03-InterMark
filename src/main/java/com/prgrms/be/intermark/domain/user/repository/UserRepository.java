package com.prgrms.be.intermark.domain.user.repository;

import com.prgrms.be.intermark.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
