package com.prgrms.be.intermark.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
