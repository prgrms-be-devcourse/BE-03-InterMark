package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.User;
import lombok.Builder;

@Builder
public record UserInfoResponseDTO(
        String nickname,
        String email
) {
    public static UserInfoResponseDTO from(User user) {
        return UserInfoResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
