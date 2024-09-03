package admin.adminbackend.repository;


import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {


    // paymentUid를 기반으로 Payment 엔티티를 조회하는 메서드
    Payment findByPaymentUid(String paymentUid);

    // 상태를 업데이트하는 메서드
    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = ?1 WHERE p.id = ?2")
    int updatePaymentStatus(PaymentStatus status, Long id);
}
