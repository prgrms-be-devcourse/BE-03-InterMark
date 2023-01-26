package com.prgrms.be.intermark.domain.user.controller;

import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

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
    public ResponseEntity<PageResponseDTO<User, UserInfoResponseDTO>> findUsers(
            @PathParam(value = "page") int page,
            @PathParam(value = "size") int size) {
        PageRequest pageRequest = pageService.getPageRequest(PageRequest.of(page, size), (int) userService.countAllUser());
        PageResponseDTO<User, UserInfoResponseDTO> users = userService.findAllUser(pageRequest);
        return ResponseEntity.ok(users);
    }
}
