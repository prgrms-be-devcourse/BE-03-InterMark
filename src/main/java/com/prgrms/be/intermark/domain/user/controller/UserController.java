package com.prgrms.be.intermark.domain.user.controller;

import com.prgrms.be.intermark.common.dto.ErrorResponse;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;

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

    @GetMapping
    public ResponseEntity<PageResponseDTO<User, UserInfoResponseDTO>> findUsers(
            @PathParam(value = "page") int page,
            @PathParam(value = "size") int size) {
        PageResponseDTO<User, UserInfoResponseDTO> users = userService.findAllUser(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse entityNotFoundExceptionHandler(EntityNotFoundException exception) {
        return ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now());
    }
}
