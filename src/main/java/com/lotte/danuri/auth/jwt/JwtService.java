package com.lotte.danuri.auth.jwt;

import com.lotte.danuri.auth.jwt.dto.LoginReqDto;

public interface JwtService {

    String createJwt(Long memberId);

    String getJwt();

    Long getMemberId() throws IllegalAccessException;

}
