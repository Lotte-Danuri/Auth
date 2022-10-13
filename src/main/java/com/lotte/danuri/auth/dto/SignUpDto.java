package com.lotte.danuri.auth.dto;

import com.lotte.danuri.auth.Auth;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpDto {

    @NotNull
    @Size(min = 5, max = 12)
    private String id;

    @NotNull
    @Size(min = 6, max = 20)
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String gender;

    private int role;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String address;

    public Auth toEntity(Long memberId, String encryptedPwd) {
        return Auth.builder()
            .loginId(id)
            .encryptedPwd(encryptedPwd)
            .role(role)
            .name(name)
            .memberId(memberId)
            .build();
    }
}
