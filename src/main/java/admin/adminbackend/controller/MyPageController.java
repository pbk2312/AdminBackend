package admin.adminbackend.controller;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.myPage.IRNotificationDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.jwt.TokenProvider;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final AuthService authService;
    private final EmailProvider emailProvider;
    private final TokenProvider tokenProvider;

    // 쿠키에서 accessToken을 추출해 사용자 인증 정보를 가져오는 메서드
    private UserDetails getUserDetailsFromToken(HttpServletRequest request) {
        String accessToken = null;

        // 쿠키에서 accessToken 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰이 유효한지 확인하고, 유효하다면 사용자 정보를 반환
        if (accessToken != null && tokenProvider.validate(accessToken)) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            return (UserDetails) authentication.getPrincipal();
        }

        return null;  // 유효하지 않은 경우 null 반환
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkMember(
            HttpServletRequest request,
            @RequestBody String password
    ) {
        UserDetails userDetails = getUserDetailsFromToken(request);

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
            HttpServletRequest request,
            @RequestBody PasswordChangeDTO passwordChangeDTO
    ) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
        }

        String username = userDetails.getUsername();
        String message = myPageService.changePassword(username, passwordChangeDTO.getChangePassword(), passwordChangeDTO.getChangeCheckPassword());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/withdrawalMembership")
    public ResponseEntity<MemberResponseDTO> withdrawalMembership(
            HttpServletRequest request,
            @RequestBody LoginDTO loginDTO) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        loginDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.withdrawalMembership(loginDTO));
    }

    @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<EmailResponseDTO> sendPasswordResetEmail(HttpServletRequest request) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        log.info("회원 탈퇴를 진행하는 이메일 : {}", userDetails.getUsername());
        EmailRequestDTO requestDTO = new EmailRequestDTO();
        requestDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.sendPasswordResetEmail(requestDTO));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> changePassword(
            HttpServletRequest request,
            @RequestBody MemberChangePasswordDTO memberChangePasswordDTO) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
        }

        String email = userDetails.getUsername();
        memberChangePasswordDTO.setEmail(email);
        String message = authService.memberChangePassword(memberChangePasswordDTO);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/memberInfo")
    public ResponseEntity<Member> memberInfo(HttpServletRequest request) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Member member = myPageService.getMemberInfo(userDetails.getUsername());
        log.info("조회 멤버....{}", member);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/IRCheck")
    public ResponseEntity<?> IRCheck(HttpServletRequest request) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
        }

        try {
            // 사용자 정보를 기반으로 멤버 객체 조회
            Member venture = myPageService.getMemberInfo(userDetails.getUsername());
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
    public ResponseEntity<String> sendIR(HttpServletRequest request,
                                         @RequestParam("IRId") Long IRId,
                                         @RequestParam("file") MultipartFile file) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
        }

        // 사용자 정보를 기반으로 멤버 객체 조회
        Member venture = myPageService.getMemberInfo(userDetails.getUsername());
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
            HttpServletRequest request,
            @RequestParam("IRId") Long IRId) {
        UserDetails userDetails = getUserDetailsFromToken(request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
        }

        IRNotification irNotification = myPageService.findIRSendMember(IRId);

        if (irNotification != null) {
            irNotification.setRead(true);
            myPageService.saveIRNotification(irNotification);
            return ResponseEntity.ok("IR 자료를 읽었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IR 자료를 찾을 수 없습니다.");
        }
    }
}