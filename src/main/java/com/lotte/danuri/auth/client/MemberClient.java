package com.lotte.danuri.auth.client;

import com.lotte.danuri.auth.dto.SignUpReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member")
public interface MemberClient {

    @PostMapping("/member/info")
    Long getInfo(@RequestBody SignUpReqDto dto);

}
