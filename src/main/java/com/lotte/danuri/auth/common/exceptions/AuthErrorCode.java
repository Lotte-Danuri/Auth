package com.lotte.danuri.auth.common.exceptions;

import com.lotte.danuri.auth.common.exceptions.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "Duplicated id")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
