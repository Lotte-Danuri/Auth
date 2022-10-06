package com.lotte.danuri.auth.common.exceptions.handler;

import com.lotte.danuri.auth.common.exceptions.ErrorResponse;
import com.lotte.danuri.auth.common.exceptions.exception.AllAuthExpiredException;
import com.lotte.danuri.auth.common.exceptions.exception.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler( InvalidRefreshTokenException.class )
    protected ResponseEntity<?> InvalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.error("InvalidRefreshTokenException", e);
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(AllAuthExpiredException.class )
    protected ResponseEntity<?> AllAuthExpiredException(AllAuthExpiredException e) {
        log.error("AllAuthExpiredException", e);
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getHttpStatus());
    }

}
