package com.prgrms.be.intermark.domain.user;

import com.prgrms.be.intermark.domain.user.dto.UpdateUserAuthorityRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // TODO: 2023-01-17 유저 ID로 변경할 수 있는 수정 api 
//    @PatchMapping("/api/v1/users/{userId}")
//    public ResponseEntity<Object> updateUserInfo(@PathVariable Long userId, @RequestBody UserInfoPatchRequestDTO userInfoPatchRequestDTO){
//        return ResponseEntity.noContent().build();
//    }

    // TODO: 2023-01-17 User ID로 해당 유저의 권한을 셋팅해주는 admin용 api 
    @PatchMapping("/api/v1/admin/users/{userId}/authority")
    public ResponseEntity<Object> updateUserAuthority(@PathVariable Long userId,@RequestBody UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO){
        userService.authorization(userId,updateUserAuthorityRequestDTO);
        return ResponseEntity.noContent().build();
    }
    // TODO: 2023-01-17 User ID로 회원 삭제


    // TODO: 2023-01-18 회원 삭제


    // TODO: 2023-01-18 회원 정보로 예매 조회
}
