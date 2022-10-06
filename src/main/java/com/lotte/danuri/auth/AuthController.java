package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.SignUpReqDto;
import com.lotte.danuri.auth.dto.TokenDto;
import java.nio.file.AccessDeniedException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users")
    public ResponseEntity<?> signUp(@RequestBody SignUpReqDto dto) {
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
