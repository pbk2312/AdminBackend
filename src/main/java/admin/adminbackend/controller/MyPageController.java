package admin.adminbackend.controller;


import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.myPage.MyPageRequestDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {


    private final MyPageService myPageService;
    private final AuthService authService;
    private final EmailProvider emailProvider;


    @PostMapping("/check")
    public ResponseEntity<String> checkMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String password
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 정보를 가져올 수 없습니다.");
        }

        String userEmail = userDetails.getUsername();
        boolean isPasswordCorrect = myPageService.checkPassword(userEmail, password);

        if (isPasswordCorrect) {
            return ResponseEntity.ok("비밀번호 확인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordChangeDTO
    ) {
        String username = userDetails.getUsername();
        String message = myPageService.changePassword(username, passwordChangeDTO.getChangePassword(), passwordChangeDTO.getChangeCheckPassword());
        return ResponseEntity.ok(message);
    }


    @PostMapping("/withdrawalMembership")
    public ResponseEntity<MemberResponseDTO> withdrawalMembership(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LoginDTO loginDTO) {
        loginDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.withdrawalMembership(loginDTO));
    }

    @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<EmailResponseDTO> sendPasswordResetEmail(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("회원 탈퇴 이메일을 전송합니다...");
        if (userDetails == null) {
            log.info("userDetails = Null");
        }
        log.info("회원 탈퇴를 진행하는 이메일 : {}", userDetails.getUsername());
        EmailRequestDTO requestDTO = new EmailRequestDTO();
        requestDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.sendPasswordResetEmail(requestDTO));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemberChangePasswordDTO memberChangePasswordDTO) {
        String email = userDetails.getUsername();
        memberChangePasswordDTO.setEmail(email);
        String message = authService.memberChangePassword(memberChangePasswordDTO);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/memberInfo")
    public ResponseEntity<Member> memberInfo(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Member member = myPageService.getMemberInfo(userDetails.getUsername());
        log.info("조회 멤버....{}", member);
        return ResponseEntity.ok(member);
    }


    // IR 온거 기업이 확인
    @GetMapping("/IRCheck")
    public ResponseEntity<List<IRNotification>> IRCheck(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 사용자 정보를 기반으로 멤버 객체 조회
        Member venture = myPageService.getMemberInfo(userDetails.getUsername());
        log.info("IR 조회 멤버: {}", venture);

        // 멤버를 기반으로 IRNotification 리스트 조회
        List<IRNotification> irList = myPageService.findIRList(venture);

        // 리스트가 비어 있는 경우, 적절한 상태 코드와 메시지를 반환할 수 있습니다.
        if (irList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // 리스트가 비어 있지 않은 경우, 200 OK와 함께 리스트를 반환
        return ResponseEntity.ok(irList);
    }


    @PostMapping("/sendIR")
    public ResponseEntity<String> sendIR(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam("IRId") Long IRId,
                                         @RequestParam("file") MultipartFile file) {

        // 사용자 정보를 기반으로 멤버 객체 조회 기업->멤버
        Member venture = myPageService.getMemberInfo(userDetails.getUsername());
        log.info("IR 전송 멤버: {}", venture);

        IRNotification irNotification = myPageService.findIRSendMember(IRId);
        Member getPerson = irNotification.getPerson();

        // 이메일 제목과 본문 구성
        String subject = "[스타트업 투자 플랫폼] IR 자료";
        String body = "안녕하세요,\n\n첨부된 파일을 확인해 주세요.\n\n감사합니다.";

        // 이메일 전송
        boolean result = emailProvider.sendFileEmail(getPerson.getEmail(), subject, body, file);

        if (result) {
            return ResponseEntity.ok("이메일 전송 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
        }
    }
}