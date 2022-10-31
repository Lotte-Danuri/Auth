package com.lotte.danuri.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenDto {

    private Long memberId;
    private String accessToken;
    private String refreshToken;
    private String encodedName;
    private String loginId;

}
