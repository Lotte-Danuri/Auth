package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.MemberInfoDto;
import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.dto.TokenDto;
import java.nio.file.AccessDeniedException;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    int signUp(SignUpDto dto);

    int checkId(String id);

    void updateRefreshToken(Long memberId, String token);

    TokenDto refresh(TokenDto dto) throws AccessDeniedException;

    AuthRespDto getUserDetailsById(String loginId);

    List<MemberInfoDto> getMembersInfo(String name);

    List<MemberInfoDto> getMembersByRole(int role);
}
