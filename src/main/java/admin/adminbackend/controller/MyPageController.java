package admin.adminbackend.controller;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.myPage.IRNotificationDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.service.MemberServiceImpl;
import admin.adminbackend.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberServiceImpl memberService;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;


    // 비밀번호 확인
    @PostMapping("/checkPassword")
    public ResponseEntity<Void> checkPassword(
            @RequestParam("password") String password,
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpSession session
    ) {
        Member member = memberService.getUserDetails(accessToken);
        validatePassword(password, member.getPassword());
        session.setAttribute("passwordChecked", true);
        return ResponseEntity.ok().build();
    }


    // 개인 정보 보기
    @GetMapping("/memberInfo")
    public String memberInfo(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpSession session,
            Model model
    ) {
        // Member 엔티티를 가져옵니다.
        Member member = memberService.getUserDetails(accessToken);


        // Member 엔티티를 MemberDTO로 변환합니다.
        MemberDTO memberDTO = member.toMemberDTO();

        // 모델에 MemberDTO를 추가합니다.
        model.addAttribute("MypageMemberDTO", memberDTO);

        // 뷰를 반환합니다.
        return "mypage/memberInfo";
    }

    @GetMapping("/IRCheck")
    public ResponseEntity<?> IRCheck(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletRequest request) {

        Member member = memberService.getUserDetails(accessToken);

        try {
            // 사용자 정보를 기반으로 멤버 객체 조회
            Member venture = myPageService.getMemberInfo(member.getEmail());
            log.info("IR 조회 요청을 받은 멤버: {}", venture);

            // 멤버를 기반으로 IRNotification 리스트 조회 및 DTO로 변환
            List<IRNotificationDTO> irNotificationDTOList = myPageService.findIRList(venture);

            if (irNotificationDTOList.isEmpty()) {
                log.info("멤버 {}의 IR 리스트가 비어 있습니다.", venture);
                return ResponseEntity.ok(Collections.emptyList());
            }

            log.info("멤버 {}의 IR 리스트를 반환합니다. 총 {} 건", venture, irNotificationDTOList.size());
            return ResponseEntity.ok(irNotificationDTOList);
        } catch (Exception e) {
            log.error("IR 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("IR 알림 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/sendIR")
    public ResponseEntity<String> sendIR(HttpServletRequest request
            , @CookieValue(value = "accessToken", required = false) String accessToken,
                                         @RequestParam("IRId") Long IRId,
                                         @RequestParam("file") MultipartFile file) {
        Member member = memberService.getUserDetails(accessToken);


        // 사용자 정보를 기반으로 멤버 객체 조회
        Member venture = myPageService.getMemberInfo(member.getEmail());
        log.info("IR 전송 멤버: {}", venture);

        IRNotification irNotification = myPageService.findIRSendMember(IRId);
        Member getPerson = irNotification.getPerson();

        String readUrl = "http://localhost:8080/mypage/readIR?IRId=" + IRId;

        String subject = "[스타트업 투자 플랫폼] IR 자료";
        String body = "안녕하세요,\n\n첨부된 파일을 확인해 주세요.\n" +
                "IR 자료를 읽으시면 다음 링크를 클릭하여 자금투자계약서를 작성해주세요: " + readUrl + "\n\n감사합니다.";

        boolean result = emailProvider.sendFileEmail(getPerson.getEmail(), subject, body, file);

        if (result) {
            return ResponseEntity.ok("이메일 전송 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
        }
    }

    @GetMapping("/readIR")
    public ResponseEntity<String> readIR(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestParam("IRId") Long IRId) {

        Member member = memberService.getUserDetails(accessToken);

        IRNotification irNotification = myPageService.findIRSendMember(IRId);

        if (irNotification != null) {
            irNotification.setRead(true);
            myPageService.saveIRNotification(irNotification);
            return ResponseEntity.ok("IR 자료를 읽었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IR 자료를 찾을 수 없습니다.");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}