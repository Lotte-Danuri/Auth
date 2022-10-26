package com.lotte.danuri.auth.oauth.kakao;

import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.SignUpByOAuthDto;

public interface KakaoService {

    String getToken(String code);

    SignUpByOAuthDto getUserInfoByKakaoToken(String accessToken);

    TokenDto kakaoLogin(SignUpByOAuthDto dto);

}
