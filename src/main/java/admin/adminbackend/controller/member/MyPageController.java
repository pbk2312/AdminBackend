package admin.adminbackend.controller.member;

import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.dto.MemberDTO;

import admin.adminbackend.dto.ResponseDTO;
import admin.adminbackend.investcontract.service.InvestmentService;
import admin.adminbackend.service.member.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;
    private final InvestmentService investmentService;


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
    public ResponseEntity<ResponseDTO<MemberDTO>> memberInfo(
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("로그인이 필요합니다.", null));
        }

        Member member = memberService.getUserDetails(accessToken);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>("회원 정보를 찾을 수 없습니다.", null));
        }

        MemberDTO memberDTO = member.toMemberDTO();

        return ResponseEntity.ok(new ResponseDTO<>("회원 정보 조회 성공", memberDTO));
    }


    // 개인정보 수정
    @PostMapping("/editMemberInfoSubmit")
    public ResponseEntity<ResponseDTO<Void>> editMemberInfoSubmit(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("address") String address
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>("로그인이 필요합니다.", null));
        }

        try {
            Member member = memberService.getUserDetails(accessToken);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>("회원 정보를 찾을 수 없습니다.", null));
            }
            LocalDate birth = LocalDate.parse(dateOfBirth); // String을 LocalDate로 변환
            // LocalDate로 변환할 필요 없이 String으로 사용
            MemberDTO mypageMemberDTO = MemberDTO.builder()
                    .email(email)
                    .name(name)
                    .nickname(nickname)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .dateOfBirth(birth) // String을 LocalDate로 변환
                    .build();

            memberService.updateMember(member, mypageMemberDTO);

            return ResponseEntity.ok(new ResponseDTO<>("회원 정보가 성공적으로 수정되었습니다.", null));
        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("회원 정보 수정 중 오류가 발생했습니다.", null));
        }
    }


    @GetMapping("/investmentHistory")
    public ResponseEntity<ResponseDTO<List<InvestmentHistoryDTO>>> getInvestmentHistory(
            @CookieValue(value = "accessToken", required = false) String accessToken) {

        // 회원 정보 가져오기
        Member member = memberService.getUserDetails(accessToken);

        // 투자 내역 가져오기 (Investment -> InvestmentHistoryDTO로 변환된 리스트)
        List<InvestmentHistoryDTO> investmentListByMemberId = investmentService.getInvestmentListByMemberId(member);

        // ResponseDTO 객체 생성 - 메시지와 데이터를 함께 전달
        ResponseDTO<List<InvestmentHistoryDTO>> response = new ResponseDTO<>("투자 내역 조회 성공", investmentListByMemberId);

        return ResponseEntity.ok(response);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}
