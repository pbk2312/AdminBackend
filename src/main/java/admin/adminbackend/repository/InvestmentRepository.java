package admin.adminbackend.repository;

import admin.adminbackend.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment,Long> {

    @Query("select o from Investment o" +
            " left join fetch o.payment p" +
            " left join fetch o.investor m" +
            " where o.investmentUid = :investmentUid")
    Optional<Investment> findInvestmentAndPaymentAndMember(@Param("investmentUid") String investmentUid);


}
