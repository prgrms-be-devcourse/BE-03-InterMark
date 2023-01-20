package com.prgrms.be.intermark.domain.user.controller;

import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    // TODO : 발생할 수 있는 EntityNotFoundException에 대한 핸들러 없는 경우 추가.
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDTO> findUser(@PathVariable Long userId) {
        UserInfoResponseDTO userInfoResponseDTO = userService.findById(userId);
        return ResponseEntity.ok(userInfoResponseDTO);
    }
}
