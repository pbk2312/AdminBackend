package admin.adminbackend.repository;

import admin.adminbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 중복 가입 방지
    Optional<Member> findByEmail(String email);

    // 존재 여부
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.password = :newPassword WHERE m.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);


}
