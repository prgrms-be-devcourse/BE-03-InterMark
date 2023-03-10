package com.prgrms.be.intermark.domain.user.repository;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
    Optional<User> findBySocialTypeAndSocialIdAndIsDeletedFalse(SocialType socialType, String socialId);

    Optional<User> findByIdAndRefreshToken(Long userId, String refreshToken);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Page<User> findByIsDeletedFalse(Pageable pageable);

    long countByIsDeletedFalse();
}
