package com.lotte.danuri.auth.oauth.common;

import com.lotte.danuri.auth.dto.TokenDto;

public interface OAuthService {

    String getToken(String code, String service);

    SignUpByOAuthDto getUserInfoFromToken(String accessToken, String service);

    TokenDto oauthLogin(SignUpByOAuthDto dto, String service);

}
