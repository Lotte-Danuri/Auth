package com.lotte.danuri.auth.common.exceptions.exception;

import com.lotte.danuri.auth.common.exceptions.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
