package com.lotte.danuri.auth.dto;

import com.lotte.danuri.auth.Member;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpReqDto {

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

    public Member toEntity() {
        return Member.builder()
            .loginId(id)
            .password(password)
            .role(role)
            .build();
    }
}
