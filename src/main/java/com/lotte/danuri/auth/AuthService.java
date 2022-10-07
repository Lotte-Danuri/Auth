package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.SignUpReqDto;
import com.lotte.danuri.auth.dto.LoginReqDto;
import com.lotte.danuri.auth.dto.TokenDto;
import java.nio.file.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    int signUp(SignUpReqDto dto);

    int checkId(String id);

    AuthRespDto getAuth(LoginReqDto dto);

    void updateRefreshToken(Long memberId, String token);

    TokenDto refresh(TokenDto dto) throws AccessDeniedException;

    AuthRespDto getUserDetailsById(String loginId);

}
