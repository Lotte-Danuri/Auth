package com.lotte.danuri.auth.oauth.common;

import org.springframework.util.MultiValueMap;

public interface OAuthDetailService {

    MultiValueMap<String, String> of(String code);

    SignUpByOAuthDto getUserInfo(String body);

    String getTokenURI();

    String getAdminKey();

    String getUserInfoURI();

}
