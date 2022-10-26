package com.lotte.danuri.auth.oauth;

import com.lotte.danuri.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpByOAuthDto {

    private Long kakaoId;
    private String email;
    private String name;
    private int role;

    private String gender;

    private String birthday;

    public Auth toEntity(Long memberId, String password) {
        return Auth.builder()
            .loginId(email)
            .encryptedPwd(password)
            .role(role)
            .name(name)
            .memberId(memberId)
            .build();
    }
}
