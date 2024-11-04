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

import java.util.Optional;
import java.util.stream.Collectors;

import admin.adminbackend.service.investment.PaymentService;
import admin.adminbackend.service.member.MemberService;
import jakarta.persistence.EntityNotFoundException;
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
    private final MemberService memberService;
    private final PaymentService paymentService;


    public void saveInvestorInvestment(InvestorInvestmentDTO investorInvestmentDTO) {
        // DTO를 엔티티로 변환
        InvestorInvestment investorInvestment = new InvestorInvestment();
        //investorInvestment.setId(investorInvestmentDTO.getId());
        investorInvestment.setInvestorName(investorInvestmentDTO.getInvestorName());
        investorInvestment.setPrice(investorInvestmentDTO.getPrice());
        investorInvestment.setAddress(investorInvestmentDTO.getAddress());
        investorInvestment.setBusinessName(investorInvestmentDTO.getBusinessName());
        //investorInvestment.setInvestmentUid(investorInvestmentDTO.getInvestmentUid());
        //investorInvestment.setInvestedAt(investorInvestmentDTO.getInvestedAt());
        // 추가 필드 설정...

        // ventureId를 사용하여 VentureListInfo 객체 설정
        if (investorInvestmentDTO.getVentureId() != null) {
            VentureListInfo ventureInfo = new VentureListInfo();
            ventureInfo.setId(investorInvestmentDTO.getVentureId()); // ventureId 설정
            investorInvestment.setVentureListInfo(ventureInfo);
        } else {
            // ventureId가 null인 경우의 처리 (예: 예외 발생 또는 기본값 설정)
            throw new IllegalArgumentException("Venture Id cannot be null");
        }

        // memberId를 사용하여 Member 엔티티 조회
        Long memberId = investorInvestmentDTO.getMemberId();
        if (memberId != null) {
            Member investor = memberService.getMemberById(memberId); // MemberService에서 ID로 Member 조회
            investorInvestment.setInvestor(investor); // Member 엔티티를 InvestorInvestment에 설정
        } else {
            // memberId가 null인 경우의 처리 (예: 예외 발생 또는 기본값 설정)
            throw new IllegalArgumentException("Member ID cannot be null");
        }

        // investorInvestmentDTO에서 paymentId 가져오기
        log.info("DTO paymentId:{}", investorInvestmentDTO.getPaymentId());
        Long paymentId = investorInvestmentDTO.getPaymentId();
        if (paymentId != null) {
            // paymentId를 이용해 Payment 엔티티 조회
            Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

            if (paymentOptional.isPresent()) {
                // payment 엔티티가 존재할 경우
                Payment payment = paymentOptional.get();

                // 조회된 Payment 엔티티에 대한 추가 처리 로직
                log.info("Payment found: " + payment);
                investorInvestment.setPayment(payment);
                log.info("엔티티 paymentId:{}", investorInvestment.getPayment().getPaymentId());

            } else {
                // paymentId에 해당하는 엔티티가 없을 경우 처리
                throw new EntityNotFoundException("Payment not found with id: " + paymentId);
            }
        } else {
            // paymentId가 null일 경우 예외 처리 또는 다른 로직 수행
            log.info("No paymentId provided in investorInvestmentDTO.");
        }

        investorInvestment.setInvestedAt(LocalDateTime.now());
        investorInvestment.setInvestmentUid(UUID.randomUUID().toString());

        log.info("투자자 정보:{}", investorInvestment.getInvestorName()); //추가

        // 데이터베이스에 저장
        investorInvestmentRepository.save(investorInvestment);
    }

    public void saveVentureInvestment(VentureInvestmentDTO ventureInvestmentDTO) {
        // DTO를 엔티티로 변환
        VentureInvestment ventureInvestment = new VentureInvestment();
        ventureInvestment.setName(ventureInvestmentDTO.getName());
        ventureInvestment.setAddress(ventureInvestmentDTO.getAddress());
        ventureInvestment.setBusinessName(ventureInvestmentDTO.getBusinessName());
        // 추가 필드 설정...

        if (ventureInvestmentDTO.getVentureId() != null) {
            VentureListInfo ventureInfo = new VentureListInfo();
            ventureInfo.setId(ventureInvestmentDTO.getVentureId()); // ventureId 설정
            ventureInvestment.setVentureListInfo(ventureInfo);
        }



        // 데이터베이스에 저장
        ventureInvestmentRepository.save(ventureInvestment);
    }


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