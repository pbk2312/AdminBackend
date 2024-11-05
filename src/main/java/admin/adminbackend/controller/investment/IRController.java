package admin.adminbackend.controller.investment;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.myPage.IRNotificationDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.service.venture.VentureListService;
import admin.adminbackend.service.investment.IRService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;


@RestController
@Log4j2
@RequiredArgsConstructor
public class IRController {

    private final VentureListService ventureListInfoService;
    private final MemberService memberService;
    private final IRService irService;
    private final EmailProvider emailProvider;


    // IR 요청 보내기
    @PostMapping("/IRSend")
    public ResponseEntity<String> sendIR(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestParam("id") Long ventureId
    ) {
        try {
            log.info("IR 요청");
            // 쿠키에서 인증 정보 가져오기
            Member member = memberService.getUserDetails(accessToken);
            VentureListInfo ventureInfo = ventureListInfoService.getCompanyById(ventureId);
            log.info("ventureInfo : {} ", ventureInfo.getMember().getName());
            Member CEO = ventureInfo.getMember(); // 대표님
            boolean success = irService.IRSend(CEO, member, ventureInfo);

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

    // 기업 입장에서 들어온 IR 체크
    @GetMapping("/IRCheck")
    public ResponseEntity<?> IRCheck(
            @CookieValue(value = "accessToken", required = false) String accessToken) {

        // ceo가 IR 달라고 요청한 멤버들이 있는지 확인
        Member ceo = memberService.getUserDetails(accessToken);

        try {
            // 멤버를 기반으로 IRNotification 리스트 조회 및 DTO로 변환
            List<IRNotificationDTO> irNotificationDTOList = irService.findIRList(ceo);

            if (irNotificationDTOList.isEmpty()) {
                log.info("멤버 {}의 IR 리스트가 비어 있습니다.", ceo);
                return ResponseEntity.ok(Collections.emptyList());
            }

            log.info("멤버 {}의 IR 리스트를 반환합니다. 총 {} 건", ceo, irNotificationDTOList.size());
            return ResponseEntity.ok(irNotificationDTOList);
        } catch (Exception e) {
            log.error("IR 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("IR 알림 조회 중 오류가 발생했습니다.");
        }
    }


    // 보낸 IR 체크
    @GetMapping("/IRSendCheck")
    public ResponseEntity<?> sendIRCheck(@CookieValue(value = "accessToken", required = false) String accessToken) {

        Member member = memberService.getUserDetails(accessToken);

        try {
            // 멤버를 기반으로 IRNotification 리스트 조회 및 DTO로 변환
            List<IRNotificationDTO> irNotificationDTOList = irService.findIRMemberList(member);

            if (irNotificationDTOList.isEmpty()) {
                log.info("멤버 {}의 IR 리스트가 비어 있습니다.", member);
                return ResponseEntity.ok(Collections.emptyList());
            }

            log.info("멤버 {}의 IR 리스트를 반환합니다. 총 {} 건", member, irNotificationDTOList.size());
            return ResponseEntity.ok(irNotificationDTOList);
        } catch (Exception e) {
            log.error("IR 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("IR 알림 조회 중 오류가 발생했습니다.");
        }

    }


    // 기업이 마이페이지에서 IR 전송
    @PostMapping("/sendIR")
    public ResponseEntity<String> sendIR(@CookieValue(value = "accessToken", required = false) String accessToken,
                                         @RequestParam("Id") Long IRId,
                                         @RequestParam("file") MultipartFile file) {
        Member ceo = memberService.getUserDetails(accessToken);
        log.info("IR 전송 멤버: {}", ceo);

        IRNotification irNotification = irService.findIRSendMember(IRId);
        Member getPerson = irNotification.getMember();

        String subject = emailProvider.createEmailSubject();
        String body = emailProvider.createEmailBody(ceo.getVentureListInfo().getId());

        boolean result = emailProvider.sendFileEmail(getPerson.getEmail(), subject, body, file);

        if (result) {
            return ResponseEntity.ok("이메일 전송 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
        }
    }





}
