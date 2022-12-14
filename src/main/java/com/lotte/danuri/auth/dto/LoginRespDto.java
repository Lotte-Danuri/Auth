package com.lotte.danuri.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginRespDto {

    private Long memberId;

    private String name;

    private String token;

}
