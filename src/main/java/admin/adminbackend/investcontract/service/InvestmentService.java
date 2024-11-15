package admin.adminbackend.investcontract.service;

import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.investcontract.domain.VentureInvestment;
import admin.adminbackend.investcontract.dto.InvestorInvestmentDTO;
import admin.adminbackend.investcontract.dto.VentureInvestmentDTO;
import admin.adminbackend.investcontract.repository.VentureInvestmentRepository;
import admin.adminbackend.repository.ventrue.VentureListInfoRepository;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
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

    private final InvestorInvestmentRepository investorInvestmentRepository;
    private final VentureListInfoRepository ventureListInfoRepository;
    private final PaymentRepository paymentRepository;
    private final VentureInvestmentRepository ventureInvestmentRepository;


    public void saveInvestorInvestment(InvestorInvestmentDTO investorInvestmentDTO, Member member) {
        // DTO를 엔티티로 변환
        InvestorInvestment investorInvestment = new InvestorInvestment();
        investorInvestment.setInvestorName(investorInvestmentDTO.getInvestorName());
        investorInvestment.setTotalPrice(investorInvestmentDTO.getPrice());
        investorInvestment.setAddress(investorInvestmentDTO.getAddress());
        investorInvestment.setBusinessName(investorInvestmentDTO.getBusinessName());

        // ventureId를 사용하여 VentureListInfo 객체 설정
        if (investorInvestmentDTO.getVentureId() != null) {
            VentureListInfo ventureInfo = new VentureListInfo();
            ventureInfo.setId(investorInvestmentDTO.getVentureId()); // ventureId 설정
            investorInvestment.setVentureListInfo(ventureInfo);
        } else {
            // ventureId가 null인 경우의 처리 (예: 예외 발생 또는 기본값 설정)
            throw new IllegalArgumentException("Venture Id cannot be null");
        }

        investorInvestment.setInvestor(member); // Member 엔티티를 InvestorInvestment에 설정

        // investorInvestmentDTO에서 paymentId 가져오기
        log.info("DTO paymentId:{}", investorInvestmentDTO.getPaymentId());

        investorInvestment.setInvestedAt(LocalDateTime.now());
        investorInvestment.setInvestmentUid(UUID.randomUUID().toString());

        log.info("투자자 정보:{}", investorInvestment.getInvestorName()); //추가

        // 데이터베이스에 저장
        investorInvestmentRepository.save(investorInvestment);
    }

    public Long saveVentureInvestment(VentureInvestmentDTO ventureInvestmentDTO,Member member) {
        // DTO를 엔티티로 변환
        VentureInvestment ventureInvestment = new VentureInvestment();
        ventureInvestment.setName(ventureInvestmentDTO.getName());
        ventureInvestment.setAddress(ventureInvestmentDTO.getAddress());
        ventureInvestment.setBusinessName(ventureInvestmentDTO.getBusinessName());

        if (ventureInvestmentDTO.getVentureId() != null) {
            VentureListInfo ventureInfo = new VentureListInfo();
            ventureInfo.setId(ventureInvestmentDTO.getVentureId()); // ventureId 설정
            ventureInvestment.setVentureListInfo(ventureInfo);
        }
        // 데이터베이스에 저장
        VentureInvestment savedVenture = ventureInvestmentRepository.save(ventureInvestment);

        return savedVenture.getId();
    }

    @Transactional
    public InvestorInvestment createInvestment(Member member, Long ventureId, Long price, String address,
                                               String getBusinessName) {
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
        investorInvestment.setTotalPrice(price);
        investorInvestment.setInvestedAt(LocalDateTime.now());
        investorInvestment.setPayment(paymentSave);
        investorInvestment.setAddress(address);
        investorInvestment.setBusinessName(getBusinessName);
        // Investment 저장
        return investorInvestmentRepository.save(investorInvestment);
    }

    @Transactional
    public List<InvestmentHistoryDTO> getInvestmentListByMemberId(Member member) {
        // 투자 내역 가져오기
        List<InvestorInvestment> investorInvestmentList = investorInvestmentRepository.findByMemberId(member.getId());

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
        dto.setAmount(investorInvestment.getTotalPrice());
        dto.setInvestedAt(investorInvestment.getInvestedAt());
        dto.setPending_status(String.valueOf(investorInvestment.getPayment().getStatus()));
        dto.setPaymentUid(investorInvestment.getPayment().getPaymentUid());
        dto.setVentureId(investorInvestment.getVentureListInfo().getId());
        dto.setInvestorId(investorInvestment.getId());

        // 투자자 이름 설정 (investment.getInvestor().getName())
        dto.setMemberName(investorInvestment.getInvestor() != null ? investorInvestment.getInvestor().getName() : null);

        // 벤처 기업 이름 설정 (investment.getVentureListInfo().getVentureName())
        dto.setVentureName(
                investorInvestment.getVentureListInfo() != null ? investorInvestment.getVentureListInfo().getName()
                        : null);

        return dto;
    }

    @Transactional(readOnly = true)
    public Long getPrice(VentureListInfo ventureListInfo) {
        return ventureListInfo.getInvestorInvestments().stream()
                .filter(investment -> investment.getPayment().getStatus() == PaymentStatus.COMPLETED)
                .mapToLong(InvestorInvestment::getTotalPrice)
                .sum();
    }

}
