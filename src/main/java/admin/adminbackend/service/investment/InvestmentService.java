package admin.adminbackend.service.investment;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.repository.ventrue.VentureListInfoRepository;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.repository.member.MemberRepository;
import admin.adminbackend.repository.investment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public Investment createInvestment(Long memberId, Long ventureId, Long price) {
        // 투자자(Member) 조회
        Member investor = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 벤처 정보(VentureListInfo) 조회
        VentureListInfo ventureListInfo = ventureListInfoRepository.getReferenceById(ventureId);
        // 결제 정보 저장
        Payment payment = new Payment(price, PaymentStatus.PENDING);
        Payment paymentSave = paymentRepository.save(payment);// Payment 저장

        // Investment 엔티티 생성
        Investment investment = new Investment();
        investment.setInvestmentUid(UUID.randomUUID().toString());
        investment.setInvestor(investor);
        investment.setVentureListInfo(ventureListInfo);
        investment.setPrice(price);
        investment.setInvestedAt(LocalDateTime.now());
        investment.setPayment(paymentSave);

        // Investment 저장
        return investmentRepository.save(investment);
    }

    @Transactional
    public List<InvestmentHistoryDTO> getInvestmentListByMemberId(Member member) {
        // 투자 내역 가져오기
        List<Investment> investmentList = investmentRepository.findByMemberId(member.getId());

        // Investment -> InvestmentHistoryDTO로 변환하여 리턴
        return investmentList.stream().map(this::convertToDto).toList();
    }

    // Investment -> InvestmentHistoryDTO 변환 메소드
    private InvestmentHistoryDTO convertToDto(Investment investment) {
        InvestmentHistoryDTO dto = new InvestmentHistoryDTO();
        dto.setInvestmentUid(investment.getInvestmentUid());
        dto.setAmount(investment.getPrice());
        dto.setInvestedAt(investment.getInvestedAt());

        // 투자자 이름 설정 (investment.getInvestor().getName())
        dto.setMemberName(investment.getInvestor() != null ? investment.getInvestor().getName() : null);

        // 벤처 기업 이름 설정 (investment.getVentureListInfo().getVentureName())
        dto.setVentureName(investment.getVentureListInfo() != null ? investment.getVentureListInfo().getName() : null);

        return dto;
    }
}
