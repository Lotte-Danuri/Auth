package com.lotte.danuri.auth.client;

import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.oauth.SignUpByOAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "member")
public interface MemberClient {

    @PostMapping("/members")
    Long getInfo(@RequestBody SignUpDto dto);

    @PostMapping("/members/oAuth")
    Long getInfoByOAuth(@RequestBody SignUpByOAuthDto dto);


}
