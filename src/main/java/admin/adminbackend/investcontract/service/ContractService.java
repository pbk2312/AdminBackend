package admin.adminbackend.investcontract.service;

import admin.adminbackend.domain.Member;
import admin.adminbackend.investcontract.domain.ContractInvestment;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.investcontract.domain.VentureInvestment;
import admin.adminbackend.investcontract.dto.ContractInvestmentDTO;
import admin.adminbackend.investcontract.dto.InvestorInvestmentDTO;
import admin.adminbackend.investcontract.dto.VentureInvestmentDTO;
import admin.adminbackend.investcontract.repository.ContractInvestmentRepository;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
import admin.adminbackend.investcontract.repository.VentureInvestmentRepository;
import admin.adminbackend.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ContractService {

    private final ContractInvestmentRepository contractInvestmentRepository;
    private final PDFGenerator pdfGenerator;
    private final MemberService memberService;
    private final InvestorInvestmentRepository investorInvestmentRepository;
    private final VentureInvestmentRepository ventureInvestmentRepository;

    public ContractService(ContractInvestmentRepository contractInvestmentRepository, PDFGenerator pdfGenerator,
                           MemberService memberService, InvestorInvestmentRepository investorInvestmentRepository,
                           VentureInvestmentRepository ventureInvestmentRepository) {
        this.contractInvestmentRepository = contractInvestmentRepository;
        this.pdfGenerator = pdfGenerator;
        this.memberService = memberService;
        this.investorInvestmentRepository = investorInvestmentRepository;
        this.ventureInvestmentRepository = ventureInvestmentRepository;
    }

    // 계약 생성 메소드
    public ContractInvestment createContract(Long investorId, Long ventureId) throws IOException {
        // 투자자 정보 조회
        InvestorInvestment investorInvestment = investorInvestmentRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("투자자를 찾을 수 없습니다."));

        // 기업 정보 조회
        VentureInvestment ventureInvestment = ventureInvestmentRepository.findById(ventureId)
                .orElseThrow(() -> new RuntimeException("벤처 투자를 찾을 수 없습니다."));

        // Member에서 생년월일 조회
        Member member = investorInvestment.getInvestor();
        String userPassword = generateRandomPassword(); // 랜덤 비밀번호 생성
        String ownerPassword = "admin123"; // 고정된 암호 설정
        log.info("계약서 암호 user:{}, owner:{}", userPassword, ownerPassword);

        // 계약 객체 생성
        ContractInvestment contract = new ContractInvestment();
        contract.setInvestorInvestment(investorInvestment);
        contract.setVentureInvestment(ventureInvestment);
        contract.setContractId(UUID.randomUUID().toString());
        contract.setRandomPassword(userPassword);

        // 계약 저장
        contractInvestmentRepository.save(contract);
        log.info("투자자 정보:{}", investorInvestment);

        // PDF 생성 및 저장
        createContractWithPDF(contract, ownerPassword, userPassword);

        // 생성된 계약 객체 반환
        return contract;
    }

    // 랜덤 비밀번호 생성 메서드
    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8); // 8자리 랜덤 문자열 생성
    }

    private void createContractWithPDF(ContractInvestment contract, String ownerPassword, String userPassword) throws IOException {
        // 계약 정보를 DTO로 변환
        ContractInvestmentDTO contractDTO = convertToDTO(contract);

        // 투자자 및 벤처 정보 설정
        InvestorInvestmentDTO investorInvestmentDTO = contractDTO.getInvestorInvestmentDTO();
        VentureInvestmentDTO ventureInvestmentDTO = contractDTO.getVentureInvestmentDTO();

        // PDF 생성 로직
        pdfGenerator.generateFinalContract(contract.getId(), contract.getContractId(), contractDTO, investorInvestmentDTO, ventureInvestmentDTO, ownerPassword, userPassword);
    }

    private ContractInvestmentDTO convertToDTO(ContractInvestment contract) {
        ContractInvestmentDTO dto = new ContractInvestmentDTO();
        dto.setContractId(contract.getContractId());
        dto.setInvestorInvestmentId(contract.getInvestorInvestment().getInvestor().getId());
        dto.setVentureInvestmentId(contract.getVentureInvestment().getVentureListInfo().getId());

        // 투자자 DTO 설정
        InvestorInvestmentDTO investorDTO = new InvestorInvestmentDTO();
        investorDTO.setInvestorName(contract.getInvestorInvestment().getInvestor().getName());
        investorDTO.setAddress(contract.getInvestorInvestment().getAddress());
        investorDTO.setPrice(contract.getInvestorInvestment().getTotalPrice());
        investorDTO.setBusinessName(contract.getInvestorInvestment().getBusinessName());
        // 다른 필드 설정
        dto.setInvestorInvestmentDTO(investorDTO);

        // 벤처 DTO 설정
        VentureInvestmentDTO ventureDTO = new VentureInvestmentDTO();
        ventureDTO.setBusinessName(contract.getVentureInvestment().getBusinessName());
        ventureDTO.setAddress(contract.getVentureInvestment().getAddress());
        ventureDTO.setName(contract.getVentureInvestment().getName());
        // 다른 필드 설정
        dto.setVentureInvestmentDTO(ventureDTO);

        return dto;
    }


}