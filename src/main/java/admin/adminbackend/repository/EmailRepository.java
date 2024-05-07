package admin.adminbackend.repository;


import admin.adminbackend.domain.EmailCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface EmailRepository extends JpaRepository<EmailCertification,String> {
    Optional<EmailCertification> findByCertificationEmail(String email);

}
