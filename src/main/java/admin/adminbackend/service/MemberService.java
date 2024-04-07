package admin.adminbackend.service;


import admin.adminbackend.dto.MemberResponseDTO;
import admin.adminbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDTO findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .map(MemberResponseDTO::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없다"));
    }

    public MemberResponseDTO findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponseDTO::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없다"));
    }


}
