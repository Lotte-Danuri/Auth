package com.lotte.danuri.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.lotte.danuri.auth.common.exceptions.exception.DuplicatedIdException;
import com.lotte.danuri.auth.dto.MemberInfoRespDto;
import com.lotte.danuri.auth.dto.SignUpDto;
import java.util.List;
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
        SignUpDto dto = SignUpDto.builder()
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

    @Test
    void 아이디_중복() {

        String id = "aaa";

        Assertions.assertThatThrownBy(() -> authService.checkId(id))
            .isInstanceOf(DuplicatedIdException.class)
            .hasMessageContaining("Duplicated id");
    }

    @Test
    void 회원정보_조회_쿠폰발급용() {

        String name = "안채영";

        List<MemberInfoRespDto> result = authService.getMembersInfo(name);
        result.forEach(m -> System.out.println(m.getName() + " " + m.getLoginId()));

        assertThat(result.size()).isGreaterThanOrEqualTo(0);
    }

}
