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
public class Member extends BaseEntity {

    private String loginId;

    private String password;

    private int role;

    private LocalDateTime deletedDate;

}
