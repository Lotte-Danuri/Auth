package com.lotte.danuri.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberInfoRespDto {

    private Long id;
    private String name;
    private String loginId;

}
