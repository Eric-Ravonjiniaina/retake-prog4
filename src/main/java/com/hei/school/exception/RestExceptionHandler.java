package com.hei.school.exception;

import com.hei.school.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequest(Exception e) {
    return ErrorResponse.builder().message(e.getMessage()).build();
  }
}
