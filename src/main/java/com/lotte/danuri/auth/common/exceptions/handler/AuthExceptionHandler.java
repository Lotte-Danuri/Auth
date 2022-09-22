package com.lotte.danuri.auth.common.exceptions.handler;

import com.lotte.danuri.auth.common.exceptions.ErrorResponse;
import com.lotte.danuri.auth.common.exceptions.exception.DuplicatedIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(DuplicatedIdException.class)
    public ResponseEntity<?> handleDuplicatedIdException(DuplicatedIdException e) {
        log.error("handleDuplicatedIdException", e);
        return new ResponseEntity<>(new ErrorResponse(e.getErrorCode()), e.getErrorCode()
            .getHttpStatus());
    }

}
