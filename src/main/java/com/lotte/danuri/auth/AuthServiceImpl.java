package com.lotte.danuri.auth;

import com.lotte.danuri.auth.client.MemberClient;
import com.lotte.danuri.auth.common.exceptions.code.AuthErrorCode;
import com.lotte.danuri.auth.common.exceptions.code.CommonErrorCode;
import com.lotte.danuri.auth.common.exceptions.exception.AllAuthExpiredException;
import com.lotte.danuri.auth.common.exceptions.exception.DuplicatedIdException;
import com.lotte.danuri.auth.common.exceptions.exception.InvalidRefreshTokenException;
import com.lotte.danuri.auth.common.exceptions.exception.WrongLoginInfoException;
import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.MemberInfoRespDto;
import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.security.TokenProvider;
import com.lotte.danuri.auth.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;

    private final MemberClient memberClient;

    private final Environment env;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    private final CircuitBreakerFactory circuitBreakerFactory;

    @Builder
    public AuthServiceImpl(AuthRepository authRepository, MemberClient memberClient,
        Environment env,
        TokenProvider tokenProvider, BCryptPasswordEncoder passwordEncoder,
        CircuitBreakerFactory circuitBreakerFactory) {
        this.authRepository = authRepository;
        this.memberClient = memberClient;
        this.env = env;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public int signUp(SignUpDto dto) {

        log.info("Before Call [getInfo] Method IN [Member-Service]");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        Long memberId = circuitBreaker.run(() -> memberClient.getInfo(dto), throwable -> 0L);
        log.info("After Call [getNames] Method IN [Member-Service]");

        String encryptedPwd = passwordEncoder.encode(dto.getPassword());
        Auth auth = dto.toEntity(memberId, encryptedPwd);

        // 아이디, 패스워드, 역할, 회원ID, 이름만 auth의 테이블에 저장
        Auth saved = authRepository.save(auth);

        return 1;
    }

    @Override
    public int checkId(String id) {
        if(authRepository.findByLoginIdAndDeletedDateIsNull(id).isPresent()) {
            throw new DuplicatedIdException(AuthErrorCode.DUPLICATED_LOGIN_ID.getMessage(), AuthErrorCode.DUPLICATED_LOGIN_ID);
        }
        return 1;
    }

    @Override
    public void updateRefreshToken(Long memberId, String token) {
        Auth user = authRepository.findByMemberIdAndDeletedDateIsNull(memberId).orElseThrow();
        user.update(token);
    }

    @Override
    public TokenDto refresh(TokenDto dto) throws AccessDeniedException {

        String newAccessToken = "";
        String refreshToken = dto.getRefreshToken();
        //String accessToken = dto.getAccessToken();

        if(validateTokenExceptionExpiration(refreshToken)) {
            Auth user = authRepository.findByMemberIdAndDeletedDateIsNull(dto.getMemberId()).orElseThrow();
            String savedRefreshToken = user.getRefreshToken();

            if(savedRefreshToken.equals(refreshToken)) {
                newAccessToken = tokenProvider.createAccessToken(dto.getMemberId());
            }else {
                throw new InvalidRefreshTokenException(
                    CommonErrorCode.BAD_REQUEST_REFRESH_TOKEN.getMessage(),
                    CommonErrorCode.BAD_REQUEST_REFRESH_TOKEN);
            }

        }else {
            throw new AllAuthExpiredException(
                CommonErrorCode.EXPIRED_REFRESH_TOKEN.getMessage(),
                CommonErrorCode.EXPIRED_REFRESH_TOKEN
            );
        }

        return TokenDto.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Override
    public AuthRespDto getUserDetailsById(String loginId) {
        log.info("Call AuthService getUserDetailsById");
        Auth user = authRepository.findByLoginIdAndDeletedDateIsNull(loginId).orElseThrow(
            () -> new WrongLoginInfoException(CommonErrorCode.BAD_REQUEST_LOGIN.getMessage(),
                CommonErrorCode.BAD_REQUEST_LOGIN)
        );

        return AuthRespDto.builder()
            .id(user.getLoginId())
            .encryptedPwd(user.getEncryptedPwd())
            .memberId(user.getMemberId())
            .name(user.getName())
            .role(user.getRole())
            .build();
    }

    @Override
    public List<MemberInfoRespDto> getMembersInfo(String name) {
        List<Auth> authList = authRepository.findByNameAndDeletedDateIsNull(name).orElseGet(ArrayList::new);

        return authList.stream().map(auth -> MemberInfoRespDto.builder()
                                        .name(auth.getName())
                                        .loginId(auth.getLoginId())
                                        .build()).collect(Collectors.toList());

    }

    public boolean validateTokenExceptionExpiration(String token) {

        // 로그아웃 확인 필요

        Jws<Claims> claims = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
            .parseClaimsJws(token);

        return claims.getBody().getExpiration().after(new Date());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Call UserService loadUserByUsername");
        Auth user = authRepository.findByLoginIdAndDeletedDateIsNull(username).orElseThrow(
            () -> new WrongLoginInfoException(CommonErrorCode.BAD_REQUEST_LOGIN.getMessage(),
            CommonErrorCode.BAD_REQUEST_LOGIN)
        );

        return new User(user.getLoginId(), user.getEncryptedPwd(),
            true, true, true, true,
            new ArrayList<>());
    }

}
