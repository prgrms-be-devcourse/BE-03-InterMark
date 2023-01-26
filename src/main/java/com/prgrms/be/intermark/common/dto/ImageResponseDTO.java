package com.prgrms.be.intermark.common.dto;

import lombok.Builder;

@Builder
public record ImageResponseDTO(String originalFileName, String path) {
}
