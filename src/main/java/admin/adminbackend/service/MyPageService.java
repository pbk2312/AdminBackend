package admin.adminbackend.service;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.myPage.MyPageRequestDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.myPage.PasswordCheckDTO;
import admin.adminbackend.repository.IRNotificationRepository;
import admin.adminbackend.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Condition;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRNotificationRepository irNotificationRepository;

    public boolean checkPassword(String userEmail, String passwordJson) {
        log.info("비밀번호 확인 요청 - 이메일: {}", userEmail);
        Optional<Member> optionalMember = memberRepository.findByEmail(userEmail);
        if (!optionalMember.isPresent()) {
            log.error("사용자를 찾을 수 없습니다. 이메일: {}", userEmail);
            return false;
        }

        Member member = optionalMember.get();
        String storedPassword = member.getPassword();

        ObjectMapper objectMapper = new ObjectMapper();
        String password;
        try {
            PasswordCheckDTO passwordCheckDTO = objectMapper.readValue(passwordJson, PasswordCheckDTO.class);
            password = passwordCheckDTO.getPassword();
        } catch (Exception e) {
            log.error("비밀번호 JSON 파싱 오류", e);
            return false;
        }

        log.info("입력된 비밀번호: {}", password);
        log.info("저장된 비밀번호: {}", storedPassword);

        boolean matches = passwordEncoder.matches(password, storedPassword);
        log.info("비밀번호 일치 여부: {}", matches);

        if (!matches) {
            log.error("비밀번호가 일치하지 않습니다. 이메일: {}", userEmail);
        } else {
            log.info("비밀번호가 일치합니다. 이메일: {}", userEmail);
        }

        return matches;
    }

    @Transactional
    public String changePassword(String email, String newPassword, String checkPassword) {
        log.info("비밀번호 변경 시도 - 이메일: {}", email);


        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!newPassword.equals(checkPassword)) {
            log.error("입력한 새 비밀번호가 일치하지 않습니다. 이메일: {}", email);
            throw new IllegalArgumentException("입력한 새 비밀번호가 일치하지 않습니다.");
        }


        if (!optionalMember.isPresent()) {
            log.error("사용자를 찾을 수 없습니다. 이메일: {}", email);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(newPassword);
        memberRepository.updatePasswordByEmail(email, hashedPassword);

        log.info("비밀번호 변경 완료 - 이메일: {}", email);
        return "비밀번호 변경이 완료되었습니다.";
    }


    @Transactional
    public Member getMemberInfo(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            log.error("사용자를 찾을 수 없습니다. 이메일: {}", memberEmail);
            return null;
        }

        return optionalMember.get();
    }

    @Transactional
    public boolean IRSend(Member member, Member shipper) {
        try {
            // IRNotification 생성 및 저장 로직
            IRNotification notification = new IRNotification();
            notification.setMember(member);
            notification.setShipper(shipper);
            notification.setRead(false); // 기본적으로 읽지 않은 상태로 설정
            // Save notification to the repository
            irNotificationRepository.save(notification);
            return true; // 성공
        } catch (Exception e) {
            // 예외 처리 및 로그 기록
            e.printStackTrace();
            return false; // 실패
        }
    }

    @Transactional
    public List<IRNotification> findIRList(Member member) {
        List<IRNotification> byMemberAndIsReadFalse = irNotificationRepository.findByMemberAndIsReadFalse(member);
        return byMemberAndIsReadFalse;
    }


}
