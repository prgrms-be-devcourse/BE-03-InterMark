package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.User;
import lombok.Builder;

@Builder
public record UserInfoResponseDTO(
        String username,
        String email
) {
    public static UserInfoResponseDTO from(User user) {
        // TODO : 예매 Dto 이용해서 변환해서 값 반환하기.
        return UserInfoResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
