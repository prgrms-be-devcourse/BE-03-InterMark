package com.prgrms.be.intermark.domain.user;

import com.prgrms.be.intermark.domain.user.dto.UpdateUserAuthorityRequestDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    @Transactional
    public void authorization(Long userId, UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO) {
        UserRole newAuthority = updateUserAuthorityRequestDTO.authority();

        Optional<User> foundedUser = repository.findById(userId);
        foundedUser.map(user->{
            user.setRole(newAuthority);
            return user;
        }).orElseThrow();
    }
}
