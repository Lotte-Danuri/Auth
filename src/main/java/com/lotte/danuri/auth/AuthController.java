package com.lotte.danuri.auth;

import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.SignUpReqDto;
import com.lotte.danuri.auth.jwt.JwtService;
import com.lotte.danuri.auth.jwt.dto.LoginReqDto;
import com.lotte.danuri.auth.jwt.dto.LoginRespDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpReqDto dto) {
        authService.checkId(dto.getId());

        return new ResponseEntity<>(authService.signUp(dto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDto dto) {
        AuthRespDto auth = authService.getAuth(dto);

        final String token = jwtService.createJwt(auth.getMemberId());
        final LoginRespDto loginRespDto = LoginRespDto.builder()
            .name(auth.getName())
            .token(token)
            .build();

        return new ResponseEntity<>(loginRespDto, HttpStatus.OK);
    }
}
