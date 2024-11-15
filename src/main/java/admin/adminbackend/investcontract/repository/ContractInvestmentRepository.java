package admin.adminbackend.investcontract.repository;

import admin.adminbackend.investcontract.domain.ContractInvestment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractInvestmentRepository extends JpaRepository<ContractInvestment, Long> {
    boolean existsByInvestorInvestmentIdAndIsGenerated(Long investorInvestmentId, boolean isGenerated);
    Optional<ContractInvestment> findByContractId(String contractId);   // contractId로 계약서 조회
}
