package admin.adminbackend.investcontract.repository;

import admin.adminbackend.investcontract.domain.InvestorInvestment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public interface InvestorInvestmentRepository extends JpaRepository<InvestorInvestment, Long> {

    @Query("select o from InvestorInvestment o" +
            " left join fetch o.payment p" +
            " left join fetch o.investor m" +
            " where o.investmentUid = :investmentUid")
    Optional<InvestorInvestment> findInvestmentAndPaymentAndMember(@Param("investmentUid") String investmentUid);

    // 단순히 investmentUid로 Investment 조회
    Optional<InvestorInvestment> findByInvestmentUid(String investmentUid);

    // memberId로 Investment 리스트를 조회하는 쿼리
    @Query("select i from InvestorInvestment i" +
            " where i.investor.id = :memberId")
    List<InvestorInvestment> findByMemberId(@Param("memberId") Long memberId);

}
