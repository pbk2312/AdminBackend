package admin.adminbackend.repository.investment;

import admin.adminbackend.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment,Long> {

    @Query("select o from Investment o" +
            " left join fetch o.payment p" +
            " left join fetch o.investor m" +
            " where o.investmentUid = :investmentUid")
    Optional<Investment> findInvestmentAndPaymentAndMember(@Param("investmentUid") String investmentUid);

    // memberId로 Investment 리스트를 조회하는 쿼리
    @Query("select i from Investment i" +
            " where i.investor.id = :memberId")
    List<Investment> findByMemberId(@Param("memberId") Long memberId);

}
