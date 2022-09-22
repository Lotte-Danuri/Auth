package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.SignUpReqDto;
import com.lotte.danuri.auth.jwt.dto.LoginReqDto;

public interface AuthService {

    int signUp(SignUpReqDto dto);

    int checkId(String id);

    AuthRespDto getAuth(LoginReqDto dto);

}
