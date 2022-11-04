package com.lotte.danuri.auth;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByLoginIdAndDeletedDateIsNull(String id);

    Optional<Auth> findByMemberIdAndDeletedDateIsNull(Long memberId);

    Optional<List<Auth>> findByNameAndRoleAndDeletedDateIsNull(String name, int role);
}
