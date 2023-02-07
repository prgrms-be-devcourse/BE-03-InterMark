package com.prgrms.be.intermark.domain.user.controller;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UpdateUserAuthorityRequestDTO;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PageService pageService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDTO> findUser(@PathVariable Long userId) {
        UserInfoResponseDTO userInfoResponseDTO = userService.findById(userId);
        return ResponseEntity.ok(userInfoResponseDTO);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<User, UserInfoResponseDTO>> findUsers(Pageable pageable) {
        PageRequest pageRequest = pageService.getPageRequest(pageable, (int) userService.countAllUser());
        PageResponseDTO<User, UserInfoResponseDTO> users = userService.findAllUser(pageRequest);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId, Authentication authentication) {
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        long authenticationUserId = Long.parseLong(authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String authority = authorities.toArray(new GrantedAuthority[0])[0].toString();
        log.info("{} {}", authentication1, authentication);
        if (UserRole.ROLE_ADMIN == UserRole.valueOf(authority)) {
            userService.delete(userId);
        } else if (userId == authenticationUserId) {
            userService.delete(userId);
        } else {
            throw new AccessDeniedException("해당 동작을 수행하기 위한 권한이 없습니다");
        }
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{targetId}/authority")
    public ResponseEntity<Object> updateRole(@PathVariable Long targetId, @Valid @RequestBody UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO, Authentication authentication) {
        String userAuthority = authentication.getAuthorities().toArray(new GrantedAuthority[0])[0].toString();
        if (UserRole.ROLE_ADMIN != UserRole.valueOf(userAuthority)) {
            throw new AccessDeniedException("해당 동작을 수행하기 위한 권한이 없습니다.");
        } else {
            userService.updateRole(targetId, updateUserAuthorityRequestDTO.authority());
        }
        return ResponseEntity.ok().build();
    }
}
