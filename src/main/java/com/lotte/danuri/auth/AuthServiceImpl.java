package com.lotte.danuri.auth;

import com.lotte.danuri.auth.common.exceptions.AuthErrorCode;
import com.lotte.danuri.auth.common.exceptions.exception.DuplicatedIdException;
import com.lotte.danuri.auth.dto.AuthRespDto;
import com.lotte.danuri.auth.dto.SignUpReqDto;
import com.lotte.danuri.auth.jwt.dto.LoginReqDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;

    @Override
    public int signUp(SignUpReqDto dto) {

        // Member 서버에 회원 개인정보 API로 전송 필요
        Long memberId = 1L;

        Auth auth = dto.toEntity(memberId);

        // 아이디, 패스워드, 역할, 회원ID, 이름만 auth의 테이블에 저장
        Auth saved = authRepository.save(auth);

        return 1;
    }

    @Override
    public int checkId(String id) {

        if(authRepository.findByLoginIdAndDeletedDateIsNull(id).isPresent()) {
            throw new DuplicatedIdException(AuthErrorCode.DUPLICATED_LOGIN_ID.getMessage(), AuthErrorCode.DUPLICATED_LOGIN_ID);
        }

        return 1;
    }

    @Override
    public AuthRespDto getAuth(LoginReqDto dto) {
        Auth auth = authRepository.findByLoginIdAndDeletedDateIsNull(dto.getId()).orElseThrow();

        return AuthRespDto.builder()
            .id(auth.getLoginId())
            .password(auth.getPassword())
            .memberId(auth.getMemberId())
            .name(auth.getName())
            .build();
    }
}
