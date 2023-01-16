package com.prgrms.be.intermark.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.Builder;

@Builder
public record ErrorResponse(

	int status,
	String message,
	List<FieldError> errors,
	LocalDateTime createdAt
) {

	public static ErrorResponse of(
		HttpStatus status,
		String message,
		List<FieldError> errors,
		LocalDateTime createdAt
	) {
		return new ErrorResponse(status.value(), message, errors, createdAt);
	}

	public static ErrorResponse of(
		HttpStatus status,
		String message,
		LocalDateTime createdAt
	) {
		return new ErrorResponse(status.value(), message, null, createdAt);
	}
}
