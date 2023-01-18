package com.prgrms.be.intermark.domain.user.repository;

import com.prgrms.be.intermark.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
