package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.User;

import lombok.Builder;

@Builder
public record UserFindResponseDTO() {

	public static UserFindResponseDTO from(User user) {
		return UserFindResponseDTO.builder()
			.build();
	}
}
