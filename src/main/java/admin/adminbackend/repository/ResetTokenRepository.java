package admin.adminbackend.repository;


import admin.adminbackend.domain.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken,String> {
    Optional<ResetToken> findByEmail(String email);

}
