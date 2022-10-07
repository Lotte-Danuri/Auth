package com.lotte.danuri.auth;

import com.lotte.danuri.auth.domain.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auth extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String loginId;

    @Column(nullable = false, unique = true)
    private String encryptedPwd;

    private int role;

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String name;

    private LocalDateTime deletedDate;

    @Column(nullable = true, unique = true)
    private String refreshToken;

    public void update(String token) {
        this.refreshToken = token;
    }

}
