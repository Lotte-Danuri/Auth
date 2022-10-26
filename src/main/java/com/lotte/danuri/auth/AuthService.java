package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.dto.LoginReqDto;
import com.lotte.danuri.auth.dto.TokenDto;
import com.lotte.danuri.auth.oauth.SignUpByOAuthDto;
import java.nio.file.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    int signUp(SignUpDto dto);

    int checkId(String id);

    void updateRefreshToken(Long memberId, String token);

    TokenDto refresh(TokenDto dto) throws AccessDeniedException;

    AuthRespDto getUserDetailsById(String loginId);

}
