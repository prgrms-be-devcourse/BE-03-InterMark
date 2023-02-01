package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.UserRole;
import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
public record UpdateUserAuthorityRequestDTO(@NotNull UserRole authority) {
}
