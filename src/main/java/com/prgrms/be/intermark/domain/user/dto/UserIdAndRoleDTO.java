package com.prgrms.be.intermark.domain.user.dto;

import com.prgrms.be.intermark.domain.user.UserRole;

public record UserIdAndRoleDTO(
        Long userId,
        UserRole userRole
) {
}
