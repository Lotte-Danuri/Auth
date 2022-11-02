package com.lotte.danuri.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberInfoDto {

    private Long id;
    private String name;
    private String loginId;

}
