package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.User;
import lombok.Builder;

@Builder
public record UserInfoResponseDTO(
        String username,
        String email
) {
    public static UserInfoResponseDTO from(User user) {
        return UserInfoResponseDTO.builder()
                .username(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
