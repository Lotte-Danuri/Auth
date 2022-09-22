package com.lotte.danuri.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthRespDto {

    private String id;

    private String password;

    private Long memberId;

    private String name;

}
