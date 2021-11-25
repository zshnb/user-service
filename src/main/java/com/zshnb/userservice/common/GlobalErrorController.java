package com.zshnb.userservice.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.zshnb.userservice.exception.InvalidArgumentException;
import com.zshnb.userservice.exception.PermissionDenyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handle all specify exception which will be thrown in service
 * */
@ControllerAdvice
public class GlobalErrorController extends ResponseEntityExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<Object> handleInvalidArgumentException(InvalidArgumentException e,
																 WebRequest webRequest) {
		logger.error("catch invalid argument exception", e);
		return handleExceptionInternal(e, Response.error(e.getMessage()),
			HttpHeaders.EMPTY, BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(PermissionDenyException.class)
	public ResponseEntity<Object> handlePermissionDenyException(PermissionDenyException e,
	                                                            WebRequest webRequest) {
		logger.error("catch permission deny exception", e);
		return handleExceptionInternal(e, null, HttpHeaders.EMPTY, FORBIDDEN, webRequest);
	}
}
