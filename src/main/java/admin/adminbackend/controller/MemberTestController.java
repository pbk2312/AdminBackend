package admin.adminbackend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
public class MemberTestController {

    @GetMapping("/test")
    public String testAuthentication(@AuthenticationPrincipal UserDetails userDetails) {
        // Principal 객체에서 사용자 이름을 가져와서 출력
        if (userDetails != null) {
            return "Authenticated User: " + userDetails.getUsername();
        } else {
            return "Authentication failed";
        }
    }
}
