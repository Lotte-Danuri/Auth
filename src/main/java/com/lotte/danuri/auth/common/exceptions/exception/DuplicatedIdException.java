package com.lotte.danuri.auth.common.exceptions.exception;

import com.lotte.danuri.auth.common.exceptions.code.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicatedIdException extends CustomException {

    public DuplicatedIdException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
