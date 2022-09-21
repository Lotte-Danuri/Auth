package com.lotte.danuri.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.lotte.danuri.auth.dto.SignUpReqDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Test
    void 회원가입() {
        SignUpReqDto dto = SignUpReqDto.builder()
            .id("aaa")
            .password("aaaa")
            .name("안채영")
            .role(1)
            .gender("여")
            .phoneNumber("010-1111-1111")
            .address("경기도 부천시")
            .build();

        int result = authService.signUp(dto);

        assertThat(result).isEqualTo(1);
    }
}
