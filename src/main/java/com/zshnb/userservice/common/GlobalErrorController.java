package com.zshnb.userservice.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.zshnb.userservice.exception.InvalidArgumentException;
import com.zshnb.userservice.exception.PermissionDenyException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalErrorController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<Object> handleInvalidArgumentException(InvalidArgumentException e,
																 WebRequest webRequest) {
		return handleExceptionInternal(e, Response.error(e.getMessage()),
			HttpHeaders.EMPTY, BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(PermissionDenyException.class)
	public ResponseEntity<Object> handlePermissionDenyException(PermissionDenyException e,
	                                                            WebRequest webRequest) {
		return handleExceptionInternal(e, null, HttpHeaders.EMPTY, FORBIDDEN, webRequest);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest webRequest) {
	    String error = e.getBindingResult().getFieldError().getDefaultMessage();
		return handleExceptionInternal(e, Response.error(error),
			HttpHeaders.EMPTY, BAD_REQUEST, webRequest);
	}
}
