package com.lotte.danuri.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotte.danuri.auth.AuthService;
import com.lotte.danuri.auth.client.MemberClient;
import com.lotte.danuri.auth.common.exceptions.code.CommonErrorCode;
import com.lotte.danuri.auth.common.exceptions.exception.WrongLoginInfoException;
import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.LoginReqDto;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Base64Utils;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthService authService;
    private final Environment env;
    private final TokenProvider tokenProvider;
    private final MemberClient memberClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                AuthService authService,
                                Environment env,
                                TokenProvider tokenProvider, MemberClient memberClient,
        CircuitBreakerFactory circuitBreakerFactory) {
        super(authenticationManager);
        this.authService = authService;
        this.env = env;
        this.tokenProvider = tokenProvider;
        this.memberClient = memberClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        log.info("Call AuthenticationFilter attemptAuthentication.");
        // request에서 로그인 정보 받아와서 인증하여 이메일과 패스워드 일치하는지 판단
        try {
            LoginReqDto creds = new ObjectMapper().readValue(request.getInputStream(), LoginReqDto.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(creds.getId(), creds.getPassword(), new ArrayList<>()
                )
            );

        } catch (AuthenticationException e) {
          throw new WrongLoginInfoException(CommonErrorCode.BAD_REQUEST_LOGIN.getMessage(),
              CommonErrorCode.BAD_REQUEST_LOGIN);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    // 로그인 성공 후 인증 성공여부, 실패 처리, 토큰 만료 처리 등과 같은 기능과 jwt 토큰 발행 기능을 수행
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                        HttpServletResponse response, FilterChain chain, Authentication authResult)
                        throws IOException, ServletException {

        log.info("Call AuthenticationFilter successfulAuthentication");
        String userName = ((User)authResult.getPrincipal()).getUsername();
        log.info("username = {}", userName);
        AuthRespDto userDetails = authService.getUserDetailsById(userName);
        String encodedName = Base64Utils.encodeToString(userDetails.getName().getBytes());

        log.info("encodedName : {}", encodedName);
        log.info("decodedName : {}", new String(Base64Utils.decode(encodedName.getBytes())));

        // token 생성
        String accessToken = tokenProvider.createAccessToken(userDetails.getMemberId());
        String refreshToken = tokenProvider.createRefreshToken();

        // refresh token 디비에 저장
        authService.updateRefreshToken(userDetails.getMemberId(), refreshToken);

        System.out.println("loginId = " + userDetails.getId());

        // seller 처리
        if(userDetails.getRole() == 1) {
            log.info("Before Call [getSeller] Method IN [Member-Service]");
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
            Long storeId = circuitBreaker.run(() -> memberClient.getSeller(userDetails.getMemberId()), throwable -> 0L);
            log.info("After Call [getSeller] Method IN [Member-Service]");

            log.info("seller storeId = {}", storeId);

            response.addHeader("store_id", String.valueOf(storeId));
        }

        response.addHeader("access_token", accessToken);
        response.addHeader("refresh_token", refreshToken);
        response.addHeader("loginId", userDetails.getId());
        response.addHeader("role", String.valueOf(userDetails.getRole()));
        response.addHeader("name", encodedName);


    }

}
