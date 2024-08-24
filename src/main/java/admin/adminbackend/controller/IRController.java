package admin.adminbackend.controller;


import admin.adminbackend.domain.Member;
import admin.adminbackend.openapi.dto.VentureListInfo;
import admin.adminbackend.openapi.service.VentureListInfoService;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/venture")
public class IRController {

    private final VentureListInfoService ventureListInfoService;
    private final MyPageService myPageService;



    // IR 보내기
    @PostMapping("/info")
    public ResponseEntity<String> sendIR(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("ventureName") String ventureName
    ) {
        try {
            log.info("verntureName = {} " ,ventureName);
            VentureListInfo ventureInfo = ventureListInfoService.getCompanyByName(ventureName);
            Member member = ventureInfo.getMember();
            Member shipper = myPageService.getMemberInfo(userDetails.getUsername());

            boolean success = myPageService.IRSend(member, shipper);

            if (success) {
                return ResponseEntity.ok("IR 요청 성공");
            } else {
                return ResponseEntity.status(500).body("IR 요청 실패");
            }
        } catch (Exception e) {
            // 예외 처리 및 로그 기록
            e.printStackTrace();
            return ResponseEntity.status(500).body("IR 요청 실패");
        }
    }


}
