package admin.adminbackend.investcontract.repository;

import admin.adminbackend.investcontract.domain.ContractInvestment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface ContractInvestmentRepository extends JpaRepository<ContractInvestment, Long> {
    // 필요에 따라 추가로 쿼리 메서드를 정의할 수 있습니다.
    boolean existsByInvestorInvestmentIdAndIsGenerated(Long investorInvestmentId, boolean isGenerated);
}
