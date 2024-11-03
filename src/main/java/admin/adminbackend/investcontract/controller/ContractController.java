package admin.adminbackend.investcontract.controller;

import admin.adminbackend.investcontract.service.ContractService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
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

}
