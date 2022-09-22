package com.lotte.danuri.auth;

import com.lotte.danuri.auth.common.exceptions.AuthErrorCode;
import com.lotte.danuri.auth.common.exceptions.exception.DuplicatedIdException;
import com.lotte.danuri.auth.dto.SignUpReqDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;

    @Override
    public int signUp(SignUpReqDto dto) {

        Member member = dto.toEntity();

        // Member 서버에 회원 개인정보 API로 전송 필요

        // 아이디, 패스워드, 역할만 auth의 member 테이블에 저장
        authRepository.save(member);

        return 1;
    }

    @Override
    public int checkId(String id) {

        if(authRepository.findByLoginIdAndDeletedDateIsNull(id).isPresent()) {
            throw new DuplicatedIdException(AuthErrorCode.DUPLICATED_LOGIN_ID.getMessage(), AuthErrorCode.DUPLICATED_LOGIN_ID);
        }

        return 1;
    }
}
