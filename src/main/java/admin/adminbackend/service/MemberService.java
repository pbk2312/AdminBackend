package admin.adminbackend.service;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import admin.adminbackend.openapi.Repository.VentureListInfoRepository;
import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final VentureListInfoRepository ventureListInfoRepository;

    /**
     * 회원 가입 시 memberRole이 VENTURE일 경우 VentureListInfo도 함께 생성 및 저장
     */
    public void registerMemberWithVenture(Member member) {
        // 1. Member 테이블에 회원 정보 저장
        Member savedMember = memberRepository.save(member);

        // 2. 만약 회원의 역할이 VENTURE라면 VentureListInfo도 생성 및 저장
        if (member.getMemberRole() == MemberRole.VENTURE) {
            VentureListInfo ventureListInfo = new VentureListInfo();
            ventureListInfo.setMember(savedMember); // Member와 연결
            ventureListInfoRepository.save(ventureListInfo);
            log.info("Venture information saved for member ID: {}", savedMember.getId());
        }
    }

    // ID로 Member 조회하는 메서드
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElse(null); // 존재하지 않으면 null 반환
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }
}
