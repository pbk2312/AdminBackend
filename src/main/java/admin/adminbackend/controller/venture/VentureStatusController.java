package admin.adminbackend.controller.venture;

import admin.adminbackend.domain.Member;
import admin.adminbackend.service.venture.VentureStatusService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureStatusController {

    private final VentureStatusService ventureStatusService;
    private final MemberService memberService;

    @PostMapping("/api/ventureStatus")
    public JSONObject getCompanyNum(@RequestBody String b_no,
                                    @CookieValue(value = "accessToken", required = false) String accessToken
                                    ) {
        Member member = memberService.getUserDetails(accessToken);
        return ventureStatusService.getCompanyNum(b_no,member);
    }

}
