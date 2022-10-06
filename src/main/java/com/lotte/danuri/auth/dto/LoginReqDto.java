package com.lotte.danuri.auth.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginReqDto {

    @NotNull
    @Size(min = 5, max = 12)
    private String id;

    @Size(min = 6, max = 20)
    private String password;

}
