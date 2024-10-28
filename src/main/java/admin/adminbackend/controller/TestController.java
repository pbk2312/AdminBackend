package admin.adminbackend.controller;


import admin.adminbackend.domain.Investment;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.service.investment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
@RequiredArgsConstructor
public class TestController {

    private final InvestmentRepository investmentRepository;
    private final PaymentService paymentService;
    @GetMapping("/testForm")
    public String test() {
        return "test";
    }

    @GetMapping("/IRCheckTest")
    public String IRCheckTest() {
        return "IRCheckTest";
    }





    @GetMapping("/paymentPage")
    public String paymentPage(
            @RequestParam("investmentId") Long investmentId,
            @RequestParam("email") String email,
            Model model
    ) {


        // 투자 내역 조회
        Investment investment = investmentRepository.findById(investmentId).orElseThrow(null);

        if (investment == null) {
            log.error("투자 정보를 찾을 수 없습니다. investmentId: {}", investmentId);
            return "redirect:/error"; // 예약 정보가 없을 경우 오류 페이지로 리다이렉트
        }

        String investmentUid = investment.getInvestmentUid();
        log.info("investmentUid: {}", investmentUid);
        PaymentDTO requestDto = paymentService.findRequestDto(investmentUid);
        requestDto.setMemberEmail(email);
        requestDto.setVentureName(investment.getVentureListInfo().getName());
        requestDto.setTotalPrice(investment.getPrice());


        model.addAttribute("requestDto", requestDto);

        // 결제 페이지로 이동
        return "paymentTest";
    }

    @GetMapping("/testInvestment")
    public String testInvestment(){
        return "investTest";
    }
}
