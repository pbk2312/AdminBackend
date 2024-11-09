package admin.adminbackend.investcontract.controller;

import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.investcontract.repository.ContractInvestmentRepository;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
import admin.adminbackend.investcontract.service.ContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContractController {

    private final ContractService contractService;
    private final ContractInvestmentRepository contractInvestmentRepository;
    private final InvestorInvestmentRepository investorInvestmentRepository;

    public ContractController(ContractService contractService, ContractInvestmentRepository contractInvestmentRepository, InvestorInvestmentRepository investorInvestmentRepository) {
        this.contractService = contractService;
        this.contractInvestmentRepository = contractInvestmentRepository;
        this.investorInvestmentRepository = investorInvestmentRepository;
    }

    @PostMapping("/create-contract")
    public ResponseEntity<String> createContract(@RequestBody Map<String, Long> request) {
        try {
            // 요청에서 투자자 ID와 기업 ID 가져오기
            Long investorId = request.get("investorId");
            Long ventureId = request.get("ventureId");

            // 계약서 생성
            contractService.createContract(investorId, ventureId);

            return ResponseEntity.ok("계약서가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(500).body("계약서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/contract/check-new")
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


}
