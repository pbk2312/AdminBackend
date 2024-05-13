package admin.adminbackend.repository;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByBuyer(Member buyer);

    Optional<Payment> findByOrderIdAndBuyer(String orderId, Member buyer);



}
