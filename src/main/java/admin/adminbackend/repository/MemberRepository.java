package admin.adminbackend.repository;

import admin.adminbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 중복 가입 방지
    Optional<Member> findByEmail(String email);

    // 존재 여부
    boolean existsByEmail(String email);

}
