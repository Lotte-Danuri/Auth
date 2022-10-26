package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.SignUpByOAuthDto;
import com.lotte.danuri.auth.oauth.kakao.KakaoService;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
//@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final Environment env;
    private final KakaoService kakaoService;

    public AuthController(AuthService authService, Environment env, KakaoService kakaoService) {
        this.authService = authService;
        this.env = env;
        this.kakaoService = kakaoService;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service"
            + ", port(local.server.port)=" + env.getProperty("local.server.port")
            + ", port(server.port)=" + env.getProperty("server.port")
            + ", token secret=" + env.getProperty("token.secret")
            + ", token expiration time=" + env.getProperty("token.max_expiration_time")
            + ", a=" + env.getProperty("a.value"));
    }

    @PostMapping("/users")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto dto) {
        log.info(dto.getName());
        authService.checkId(dto.getId()); // 아이디 중복 체크
        return new ResponseEntity<>(authService.signUp(dto), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "access_token") String accessToken,
        @RequestHeader(value = "refresh_token") String refreshToken,
        @RequestBody TokenDto dto)
        throws AccessDeniedException {

        Long memberId = dto.getMemberId();
        TokenDto result = authService.refresh(TokenDto.builder()
            .memberId(memberId)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/oauth/code")
    public ResponseEntity<?> getCode() {

        log.info("===========> getCode()");

        String clientId = env.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
        String redirectURI =
            env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri");

        StringBuilder sb = new StringBuilder();
        sb.append("https://kauth.kakao.com/oauth/authorize?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectURI).append("&response_type=code");

        log.info("===========> getCode() finish");

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);

    }

    @GetMapping(value = "/oauth/token")
    public ResponseEntity<?> kakaoOAuthRedirect(@RequestParam String code) {
        log.info("Kakao Authorization Code = {}", code);
        String accessToken = kakaoService.getToken(code);
        SignUpByOAuthDto dto =
            kakaoService.getUserInfoByKakaoToken(accessToken);

        TokenDto result = kakaoService.kakaoLogin(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}
