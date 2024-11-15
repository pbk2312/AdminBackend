package admin.adminbackend.investcontract.controller;

import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.investcontract.domain.ContractInvestment;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.investcontract.repository.ContractInvestmentRepository;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
import admin.adminbackend.investcontract.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    private final EmailProvider emailProvider;
    private final ContractInvestmentRepository contractInvestmentRepository;
    private final InvestorInvestmentRepository investorInvestmentRepository;


    @PostMapping("/create-contract")
    public ResponseEntity<Map<String, String>> createContract(@RequestBody Map<String, Long> request) {
        try {
            // 요청에서 투자자 ID와 기업 ID 가져오기
            Long investorId = request.get("investorId");
            Long ventureId = request.get("ventureId");

            // 계약서 생성
            ContractInvestment contract = contractService.createContract(investorId, ventureId);

            InvestorInvestment investorInvestment = investorInvestmentRepository.getReferenceById(investorId);
            String investorEmail = investorInvestment.getInvestor().getEmail();
            String userPassword = contract.getRandomPassword(); // 생성된 암호 가져오기
            String contractUrl = "/api/contract/" + contract.getContractId();

            emailProvider.sendContractEmail(investorEmail, contractUrl, userPassword);


            // contractId를 포함한 JSON 형태의 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "계약서가 성공적으로 생성되었습니다.");
            response.put("Id", String.valueOf(contract.getId()));
            response.put("contractId", contract.getContractId());
            response.put("investorId", String.valueOf(contract.getInvestorInvestment().getId()));
            response.put("memberId", String.valueOf(contract.getInvestorInvestment().getInvestor().getId()));
            response.put("filePath", contract.getFilePath());
            response.put("isGenerated", String.valueOf(contract.isGenerated()));
            response.put("RandomPassword(userPassord)", userPassword);




            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "계약서 생성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @GetMapping("/contract/check-new") //오빠가 투자자 폼 입력할때 investorId를 저장하고 나중에 꺼낼수 있나?
    //안되면 토큰으로 받아서 memberId 로? 그럼 밑에랑 다른 코드도 수정..?
    public ResponseEntity<Map<String, Boolean>> checkNewContract(@RequestParam Long investorId) {
        // investorId로 InvestorInvestment 조회 (Optional로 반환)
        InvestorInvestment investorInvestment = investorInvestmentRepository.findById(investorId)
                .orElseThrow(() -> new IllegalArgumentException("InvestorInvestment not found for investorId: " + investorId));

        // 해당 InvestorInvestment의 ID로 관련된 ContractInvestment 조회
        Long investorInvestmentId = investorInvestment.getId();
        boolean hasNewContract = contractInvestmentRepository.existsByInvestorInvestmentIdAndIsGenerated(investorInvestmentId, true);

        // 결과 반환
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasNewContract", hasNewContract);

        return ResponseEntity.ok(response);
    }

    /*@GetMapping("/contract/check-new")
    public ResponseEntity<Map<String, Boolean>> checkNewContract(@CookieValue(name = "memberId") Long memberId) {
        // memberId로 InvestorInvestment 조회 (Optional로 반환)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for memberId: " + memberId));

        InvestorInvestment investorInvestment = investorInvestmentRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("InvestorInvestment not found for memberId: " + memberId));

        // 해당 InvestorInvestment의 ID로 관련된 ContractInvestment 조회
        Long investorInvestmentId = investorInvestment.getId();
        boolean hasNewContract = contractInvestmentRepository.existsByInvestorInvestmentIdAndIsGenerated(investorInvestmentId, true);

        // 결과 반환
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasNewContract", hasNewContract);

        return ResponseEntity.ok(response);
    }*/



    // 계약서 다운로드 엔드포인트
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<Resource> downloadContract(@PathVariable String contractId) throws Exception {
        // 계약서 정보 확인
        ContractInvestment contract = contractInvestmentRepository.findByContractId(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found for contractId: " + contractId));

        if (!contract.isGenerated()) {
            throw new IllegalArgumentException("Contract not generated yet.");
        }

        // 계약서 파일 경로
        Path filePath = Paths.get(contract.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Could not read the file: " + contract.getFilePath());
        }

        // 다운로드 응답 설정
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

