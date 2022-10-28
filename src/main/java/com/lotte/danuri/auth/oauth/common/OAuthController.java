package com.lotte.danuri.auth.oauth.common;

import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.kakao.KakaoAttribute;
import com.lotte.danuri.auth.oauth.naver.NaverAttribute;
import java.math.BigInteger;
import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class OAuthController {

    private final Environment env;
    private final OAuthService oAuthService;
    private final NaverAttribute naverAttribute;
    private final KakaoAttribute kakaoAttribute;

    public OAuthController(Environment env, OAuthService oAuthService,
        NaverAttribute naverAttribute,
        KakaoAttribute kakaoAttribute) {
        this.env = env;
        this.oAuthService = oAuthService;
        this.naverAttribute = naverAttribute;
        this.kakaoAttribute = kakaoAttribute;
    }

    @GetMapping("/code/kakao")
    public ResponseEntity<?> getCodeFromKakao() {

        log.info("Call getCodeFromKakao()");

        String clientId = kakaoAttribute.getClientId();
        String redirectURI = kakaoAttribute.getRedirectURI();
        String authorizationURI = kakaoAttribute.getAuthorizationURI();

        StringBuilder sb = new StringBuilder();
        sb.append(authorizationURI).append("?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectURI).append("&response_type=code");

        log.info("request URI = {}", sb);

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);

    }

    @GetMapping("/code/naver")
    public ResponseEntity<?> getCodeFromNaver() {

        log.info("Call getCodeFromNaver()");

        String clientId = naverAttribute.getClientId();
        String redirectURI = naverAttribute.getRedirectURI();
        String authorizationURI = naverAttribute.getAuthorizationURI();
        String status = new BigInteger(130, new SecureRandom()).toString();

        StringBuilder sb = new StringBuilder();
        sb.append(authorizationURI).append("?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectURI).append("&response_type=code&state=")
            .append(status);

        log.info("request URI = {}", sb);

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);

    }

    @GetMapping(value = "/token")
    public ResponseEntity<?> getToken(@RequestParam String code,
                                                @RequestParam String service) {
        log.info("OAuth {} Authorization Code = {}", service, code);

        String accessToken = oAuthService.getToken(code, service);

        SignUpByOAuthDto dto =
            oAuthService.getUserInfoFromToken(accessToken,service);

        TokenDto result = oAuthService.oauthLogin(dto, service);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
