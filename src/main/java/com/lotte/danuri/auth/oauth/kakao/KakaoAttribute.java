package com.lotte.danuri.auth.oauth.kakao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoAttribute {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectURI;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authorizationURI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenURI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoURI;

}
