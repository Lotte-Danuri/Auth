package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.SignUpDto;
import com.lotte.danuri.auth.dto.TokenDto;
import java.nio.file.AccessDeniedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final Environment env;

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service"
            + ", port(local.server.port)=" + env.getProperty("local.server.port")
            + ", port(server.port)=" + env.getProperty("server.port")
            + ", token secret=" + env.getProperty("token.secret")
            + ", token expiration time=" + env.getProperty("token.max_expiration_time")
            + ", a=" + env.getProperty("a.value"));
    }

    @PostMapping("/users")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto dto) {
        log.info(dto.getName());
        authService.checkId(dto.getId()); // 아이디 중복 체크
        return new ResponseEntity<>(authService.signUp(dto), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "access_token") String accessToken,
        @RequestHeader(value = "refresh_token") String refreshToken,
        @RequestBody TokenDto dto)
        throws AccessDeniedException {

        Long memberId = dto.getMemberId();
        TokenDto result = authService.refresh(TokenDto.builder()
            .memberId(memberId)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /*@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDto dto) {
        AuthRespDto auth = authService.getAuth(dto);

        final String token = jwtService.createJwt(auth.getMemberId());
        final LoginRespDto login
        RespDto = LoginRespDto.builder()
            .name(auth.getName())
            .token(token)
            .build();

        return new ResponseEntity<>(loginRespDto, HttpStatus.OK);
    }*/
}
