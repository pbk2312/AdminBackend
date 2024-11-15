package admin.adminbackend.controller.venture;

import admin.adminbackend.service.venture.VentureStatusService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureStatusController {

    @PostMapping("/api/ventureStatus")
    public JSONObject getCompanyNum(@RequestBody String b_no) {
        return VentureStatusService.getCompanyNum(b_no);
    }

}
