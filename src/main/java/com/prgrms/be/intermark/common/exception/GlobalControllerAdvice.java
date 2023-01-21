package com.prgrms.be.intermark.common.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.prgrms.be.intermark.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.BAD_REQUEST,
			e.getMessage(),
			e.getFieldErrors(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.BAD_REQUEST,
			e.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.BAD_REQUEST,
			e.getMessage(),
			e.getFieldErrors(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.BAD_REQUEST,
			e.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.METHOD_NOT_ALLOWED,
			e.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
				HttpStatus.NOT_FOUND,
				e.getMessage(),
				LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
				HttpStatus.BAD_REQUEST,
				e.getMessage(),
				LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
		ErrorResponse errorResponse = ErrorResponse.of(
				HttpStatus.BAD_REQUEST,
				e.getMessage(),
				LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.INTERNAL_SERVER_ERROR,
			e.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
