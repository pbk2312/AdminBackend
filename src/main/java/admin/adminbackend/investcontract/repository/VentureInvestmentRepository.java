package admin.adminbackend.investcontract.repository;

import admin.adminbackend.investcontract.domain.VentureInvestment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentureInvestmentRepository extends JpaRepository<VentureInvestment, Long> {
}
