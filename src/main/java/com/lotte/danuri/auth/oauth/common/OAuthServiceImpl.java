package com.lotte.danuri.auth.oauth.common;

import com.lotte.danuri.auth.Auth;
import com.lotte.danuri.auth.AuthRepository;
import com.lotte.danuri.auth.client.MemberClient;
import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.kakao.KakaoService;
import com.lotte.danuri.auth.oauth.naver.NaverService;
import com.lotte.danuri.auth.security.TokenProvider;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Transactional
public class OAuthServiceImpl implements OAuthService {
    private final AuthRepository authRepository;
    private final MemberClient memberClient;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final OAuthAttributeService oAuthAttributeService;
    private final KakaoService kakaoService;
    private final NaverService naverService;

    public OAuthServiceImpl(AuthRepository authRepository,
        MemberClient memberClient, TokenProvider tokenProvider,
        BCryptPasswordEncoder passwordEncoder, CircuitBreakerFactory circuitBreakerFactory,
        OAuthAttributeService oAuthAttributeService, KakaoService kakaoService,
        NaverService naverService) {
        this.authRepository = authRepository;
        this.memberClient = memberClient;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.oAuthAttributeService = oAuthAttributeService;
        this.kakaoService = kakaoService;
        this.naverService = naverService;
    }

    @Override
    public String getToken(String code, String service) {

        MultiValueMap<String, String> params = oAuthAttributeService.getParams(code, service);
        String tokenUri = oAuthAttributeService.getTokenURI(service);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request =
            new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
            tokenUri, HttpMethod.POST, request, String.class
        );

        log.info("Response from OAuth Service [{}] = {}", service, response);

        String tokenJson = response.getBody();
        JSONParser parser = new JSONParser();
        String accessToken = "";
        try {
            JSONObject object = (JSONObject) parser.parse(tokenJson);
            accessToken = (String) object.get("access_token");
            log.info("Access Token from {} = {}", service, accessToken);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return accessToken;
    }

    @Override
    public SignUpByOAuthDto getUserInfoFromToken(String accessToken, String service) {

        String userInfoUri = oAuthAttributeService.getUserInfoURI(service);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            userInfoUri,
            HttpMethod.POST,
            request,
            String.class
        );

        String body = response.getBody();
        log.info("Response Body = {}", body);

        if(service.equals("kakao")) {
            return kakaoService.getUserInfo(body);
        }else {
            return naverService.getUserInfo(body);
        }

    }

    @Override
    public TokenDto oauthLogin(SignUpByOAuthDto dto, String service) {

        final String secretKey = oAuthAttributeService.getAdminKey(service);
        Optional<Auth> auth = authRepository.findByLoginIdAndDeletedDateIsNull(dto.getEmail());

        Long memberId = 0L;
        if(auth.isEmpty()) {
            // 최초 로그인, 회원가입 진행
            log.info("Before Call [getInfoByOAuth] Method IN [Member-Service]");
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
            memberId = circuitBreaker.run(() -> memberClient.getInfoByOAuth(dto), throwable -> 0L);
            log.info("After Call [getInfoByOAuth] Method IN [Member-Service]");

            String password = dto.getId() + secretKey;
            String encryptedPwd = passwordEncoder.encode(password);

            authRepository.save(dto.toEntity(memberId, encryptedPwd));

        }else {
            memberId = auth.get().getMemberId();
        }

        String accessToken = tokenProvider.createAccessTokenByOAuth(memberId, service);
        String refreshToken = tokenProvider.createRefreshToken();

        Auth user = authRepository.findByMemberIdAndDeletedDateIsNull(memberId).orElseThrow();
        user.update(refreshToken);

        String loginId = user.getLoginId().split("@")[0];

        log.info("accessToken : {}, refreshToken : {}", accessToken, refreshToken);

        return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .encodedName(dto.getName())
            .loginId(loginId)
            .role(user.getRole())
            .build();

    }
}
