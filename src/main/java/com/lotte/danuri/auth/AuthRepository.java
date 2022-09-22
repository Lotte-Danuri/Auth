package com.lotte.danuri.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginIdAndDeletedDateIsNull(String id);
}
