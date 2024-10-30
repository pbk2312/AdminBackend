package admin.adminbackend.controller;


import admin.adminbackend.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.service.investment.PaymentService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
@RequiredArgsConstructor
public class TestController {

    private final InvestmentRepository investmentRepository;
    private final PaymentService paymentService;
    private final MemberService memberService;
    @GetMapping("/testForm")
    public String test() {
        return "test";
    }

    @GetMapping("/IRCheckTest")
    public String IRCheckTest() {
        return "IRCheckTest";
    }





    @GetMapping("/paymentPage")
    public ResponseEntity<?> paymentPage(
            @RequestParam("investmentId") Long investmentId,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {

        Member member = memberService.getUserDetails(accessToken);

        // 투자 내역 조회
        InvestorInvestment investorInvestment = investmentRepository.findById(investmentId).orElseThrow(() -> {
            log.error("투자 정보를 찾을 수 없습니다. investmentId: {}", investmentId);
            return new RuntimeException("투자 정보를 찾을 수 없습니다.");
        });

        String investmentUid = investorInvestment.getInvestmentUid();
        log.info("investmentUid: {}", investmentUid);
        PaymentDTO requestDto = paymentService.findRequestDto(investmentUid);

        // PaymentDTO에 필요한 정보 세팅
        requestDto.setMemberEmail(member.getEmail());
        requestDto.setVentureName(investorInvestment.getVentureListInfo().getName());
        requestDto.setTotalPrice(investorInvestment.getPrice());

        // JSON 응답 반환
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }

    @GetMapping("/invest")
    public String testInvestment(){
        return "investTest";
    }
}
