package com.lotte.danuri.auth.oauth.naver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NaverAttribute {

    // 네이버
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectURI;

    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String authorizationURI;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String tokenURI;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String userInfoURI;
}
