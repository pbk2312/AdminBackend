package admin.adminbackend.controller;

import admin.adminbackend.domain.Member;
import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.openapi.service.VentureListService;
import admin.adminbackend.service.MyPageService;
import admin.adminbackend.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/venture")
public class IRController {

    private final VentureListService ventureListInfoService;
    private final MyPageService myPageService;
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

    // IR 보내기
    @PostMapping("/info")
    public ResponseEntity<String> sendIR(
            HttpServletRequest request,
            @RequestParam("id") Long ventureId
    ) {
        try {
            // 쿠키에서 인증 정보 가져오기
            UserDetails userDetails = getUserDetailsFromToken(request);

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
            }

            log.info("ventureId = {}", ventureId);
            VentureListInfo ventureInfo = ventureListInfoService.getCompanyById(ventureId);
            Member member = ventureInfo.getMember();
            Member shipper = myPageService.getMemberInfo(userDetails.getUsername());

            boolean success = myPageService.IRSend(member, shipper);

            if (success) {
                return ResponseEntity.ok("IR 요청 성공");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IR 요청 실패");
            }
        } catch (Exception e) {
            // 예외 처리 및 로그 기록
            log.error("IR 요청 중 예외 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IR 요청 실패");
        }
    }
}