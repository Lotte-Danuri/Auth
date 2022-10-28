package com.lotte.danuri.auth.oauth.kakao;

import com.lotte.danuri.auth.Auth;
import com.lotte.danuri.auth.AuthRepository;
import com.lotte.danuri.auth.client.MemberClient;
import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.SignUpByOAuthDto;
import com.lotte.danuri.auth.security.TokenProvider;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Transactional
public class KakaoServiceImpl implements KakaoService{

    private final Environment env;
    private final AuthRepository authRepository;
    private final MemberClient memberClient;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public KakaoServiceImpl(Environment env, AuthRepository authRepository,
        MemberClient memberClient, TokenProvider tokenProvider,
        BCryptPasswordEncoder passwordEncoder, CircuitBreakerFactory circuitBreakerFactory) {
        this.env = env;
        this.authRepository = authRepository;
        this.memberClient = memberClient;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public String getToken(String code) {

        final String CLIENT_ID =
            env.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
        final String CLIENT_SECRET =
            env.getProperty("spring.security.oauth2.client.registration.kakao.client-secret");
        final String GRANT_TYPE = "authorization_code";
        final String SERVER_URL =
            env.getProperty("spring.security.oauth2.client.provider.kakao.token-uri");
        final String REDIRECT_URL =
            env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE);
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URL);
        params.add("code", code);
        params.add("client_secret", CLIENT_SECRET);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
            new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
            SERVER_URL, HttpMethod.POST, kakaoTokenRequest, String.class
        );

        log.info("Response from Kakao = {}", response);

        String tokenJson = response.getBody();
        JSONParser parser = new JSONParser();
        String accessToken = "";
        try {
            JSONObject object = (JSONObject) parser.parse(tokenJson);
            accessToken = (String) object.get("access_token");
            log.info("Access Token from Kakao = {}", accessToken);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return accessToken;
    }

    @Override
    public SignUpByOAuthDto getUserInfoByKakaoToken(String accessToken) {

        final String USER_INFO_URI =
            env.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            USER_INFO_URI,
            HttpMethod.POST,
            kakaoUserInfoRequest,
            String.class
        );

        String body = response.getBody();
        log.info("Response Body = {}", body);

        JSONParser parser = new JSONParser();
        Long kakaoId = 0L;
        String name = "";
        String email = "";
        String gender = "";
        String birthday = "";

        try {
            JSONObject object = (JSONObject) parser.parse(body);

            JSONObject nameObj = (JSONObject) parser.parse(object.get("properties").toString());
            JSONObject obj = (JSONObject) parser.parse(object.get("kakao_account").toString());

            kakaoId = (Long) object.get("id");
            name = (String) nameObj.get("nickname");
            email = (String) obj.get("email");
            gender = (String) obj.get("gender");
            birthday = (String) obj.get("birthday");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return SignUpByOAuthDto.builder()
            .kakaoId(kakaoId)
            .name(name)
            .email(email)
            .gender(gender)
            .birthday(birthday)
            .role(0).build();
    }

    @Override
    public TokenDto kakaoLogin(SignUpByOAuthDto dto) {

        final String ADMIN_KEY =
            env.getProperty("spring.security.oauth2.client.registration.kakao.client-secret");
        Optional<Auth> auth = authRepository.findByLoginIdAndDeletedDateIsNull(dto.getEmail());

        Long memberId = 0L;
        if(auth.isEmpty()) {
            // 최초 로그인, 회원가입 진행
            log.info("Before Call [getInfoByOAuth] Method IN [Member-Service]");
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
            memberId = circuitBreaker.run(() -> memberClient.getInfoByOAuth(dto), throwable -> 0L);
            log.info("Before Call [getInfoByOAuth] Method IN [Member-Service]");

            String password = dto.getKakaoId() + ADMIN_KEY;
            String encryptedPwd = passwordEncoder.encode(password);

            authRepository.save(dto.toEntity(memberId, encryptedPwd));

        }else {
            memberId = auth.get().getMemberId();
        }

        String accessToken = tokenProvider.createAccessTokenByOAuth(memberId, "kakao");
        String refreshToken = tokenProvider.createRefreshToken();

        Auth user = authRepository.findByMemberIdAndDeletedDateIsNull(memberId).orElseThrow();
        user.update(refreshToken);
        String encodedName = Base64Utils.encodeToString(dto.getName().getBytes());

        log.info("accessToken : {}, refreshToken : {}", accessToken, refreshToken);

        return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .encodedName(encodedName)
            .build();

    }
}
