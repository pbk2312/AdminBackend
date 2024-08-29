package admin.adminbackend.service;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.openapi.dto.VentureListInfo;
import admin.adminbackend.openapi.dto.VentureListInfoRepository;
import admin.adminbackend.repository.InvestmentRepository;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Log4j2
@RequiredArgsConstructor
@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final MemberRepository memberRepository;
    private final VentureListInfoRepository ventureListInfoRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Investment createInvestment(Long memberId, Long ventureId, Long amount) {
        // 투자자(Member) 조회
        Member investor = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 벤처 정보(VentureListInfo) 조회
        VentureListInfo ventureListInfo = ventureListInfoRepository.findById(ventureId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid venture ID"));

        // 결제 정보 저장
        Payment payment = new Payment(amount, PaymentStatus.PENDING);
        Payment paymentSave = paymentRepository.save(payment);// Payment 저장

        // Investment 엔티티 생성
        Investment investment = new Investment();
        investment.setInvestmentUid(UUID.randomUUID().toString());
        investment.setInvestor(investor);
        investment.setVentureListInfo(ventureListInfo);
        investment.setAmount(amount);
        investment.setInvestedAt(LocalDateTime.now());
        investment.setPayment(paymentSave);

        // Investment 저장
        return investmentRepository.save(investment);
    }
}
