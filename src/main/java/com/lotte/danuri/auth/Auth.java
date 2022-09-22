package com.lotte.danuri.auth;

import com.lotte.danuri.auth.domain.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Entity;
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

    private String loginId;

    private String password;

    private int role;

    private Long memberId;

    private String name;

    private LocalDateTime deletedDate;

}
