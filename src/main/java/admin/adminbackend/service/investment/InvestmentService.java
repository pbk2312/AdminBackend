package admin.adminbackend.service.investment;

import admin.adminbackend.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.repository.ventrue.VentureListInfoRepository;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.repository.investment.PaymentRepository;
import java.util.stream.Collectors;
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
    private final VentureListInfoRepository ventureListInfoRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public InvestorInvestment createInvestment(Member member, Long ventureId, Long price, String address, String getBusinessName
    ) {

        // 벤처 정보(VentureListInfo) 조회
        VentureListInfo ventureListInfo = ventureListInfoRepository.getReferenceById(ventureId);
        // 결제 정보 저장
        Payment payment = new Payment(price, PaymentStatus.PENDING);
        Payment paymentSave = paymentRepository.save(payment);// Payment 저장

        // Investment 엔티티 생성
        InvestorInvestment investorInvestment = new InvestorInvestment();
        investorInvestment.setInvestmentUid(UUID.randomUUID().toString());
        investorInvestment.setInvestor(member);
        investorInvestment.setVentureListInfo(ventureListInfo);
        investorInvestment.setPrice(price);
        investorInvestment.setInvestedAt(LocalDateTime.now());
        investorInvestment.setPayment(paymentSave);
        investorInvestment.setAddress(address);
        investorInvestment.setBusinessName(getBusinessName);
        // Investment 저장
        return investmentRepository.save(investorInvestment);
    }

    @Transactional
    public List<InvestmentHistoryDTO> getInvestmentListByMemberId(Member member) {
        // 투자 내역 가져오기
        List<InvestorInvestment> investorInvestmentList = investmentRepository.findByMemberId(member.getId());

        // Investment -> InvestmentHistoryDTO로 변환하여 리턴
        return investorInvestmentList.stream().map(this::convertToDto).toList();
    }

    @Transactional
    public List<InvestmentHistoryDTO> getVentureInvestmentListByMemberId(Member member) {

        List<InvestorInvestment> investorInvestments = member.getVentureListInfo().getInvestorInvestments();

        return investorInvestments.stream().map(this::convertToDto).collect(Collectors.toList());

    }

    // Investment -> InvestmentHistoryDTO 변환 메소드
    private InvestmentHistoryDTO convertToDto(InvestorInvestment investorInvestment) {
        InvestmentHistoryDTO dto = new InvestmentHistoryDTO();
        dto.setInvestmentUid(investorInvestment.getInvestmentUid());
        dto.setAmount(investorInvestment.getPrice());
        dto.setInvestedAt(investorInvestment.getInvestedAt());
        dto.setPending_status(String.valueOf(investorInvestment.getPayment().getStatus()));

        // 투자자 이름 설정 (investment.getInvestor().getName())
        dto.setMemberName(investorInvestment.getInvestor() != null ? investorInvestment.getInvestor().getName() : null);

        // 벤처 기업 이름 설정 (investment.getVentureListInfo().getVentureName())
        dto.setVentureName(
                investorInvestment.getVentureListInfo() != null ? investorInvestment.getVentureListInfo().getName() : null);

        return dto;
    }
}