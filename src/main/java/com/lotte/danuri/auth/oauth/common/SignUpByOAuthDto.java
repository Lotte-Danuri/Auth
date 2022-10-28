package com.lotte.danuri.auth.oauth.common;

import com.lotte.danuri.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpByOAuthDto {

    private String id;
    private String email;
    private String name;
    private int role;

    private String gender;

    private String birthday;

    private String birthyear;

    private String phone;

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
