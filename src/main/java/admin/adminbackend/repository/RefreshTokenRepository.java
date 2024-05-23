package admin.adminbackend.repository;

import admin.adminbackend.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByKey(String key);

    Optional<RefreshToken> findByEmail(String email);

    void deleteByKey(String key); // 변경된 메서드 이름

    void deleteByEmail(String email); // 새로운 삭제 메서드 추가
}
