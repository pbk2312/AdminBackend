package admin.adminbackend.service;


import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.myPage.MyPageRequestDTO;
import admin.adminbackend.dto.myPage.MyPageResponseDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String changePassword(PasswordChangeDTO passwordChangeDTO) {

        String email = passwordChangeDTO.getEmail();
        String password = passwordChangeDTO.getChangePassword();
        String checkPassword = passwordChangeDTO.getChangeCheckPassword();

        if (!password.equals(checkPassword)) {
            throw new RuntimeException("입력한 비밀번호가 일치하지 않습니다.");
        }

        //비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(password);

        memberRepository.updatePasswordByEmail(email, hashedPassword);

        return "비밀번호 변경이 완료되었습니다.";

    }


}
