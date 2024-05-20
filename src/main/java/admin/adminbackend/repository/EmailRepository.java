package admin.adminbackend.repository;


import admin.adminbackend.domain.EmailCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmailRepository extends JpaRepository<EmailCertification,String> {
    Optional<EmailCertification> findByCertificationEmail(String email);

}
