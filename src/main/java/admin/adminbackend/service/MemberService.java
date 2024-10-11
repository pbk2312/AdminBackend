package admin.adminbackend.service;

import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.dto.WithdrawalMembershipDTO;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;




public interface MemberService {

    MemberResponseDTO register(MemberRequestDTO memberRequestDTO);

    // 회원 탈퇴를 처리하는 메서드
    String deleteAccount(WithdrawalMembershipDTO withdrawalMembershipDTO);
    EmailResponseDTO sendCertificationMail(EmailRequestDTO emailRequestDTO);

    // 비밀번호 재설정을 위한 이메일을 발송하는 메서드
    String sendPasswordResetEmail(EmailRequestDTO emailRequestDTO);

    TokenDTO login(LoginDTO loginDTO);

    void logout(LogoutDTO logoutDTO);

    String memberChangePassword(MemberChangePasswordDTO memberChangePasswordDTO);


    Member getUserDetails(String accessToken);

    Member findByEmail(String email);

    Member findByRefreshToken(String refreshToken);
    void updateMember(Member member, MemberDTO mypageMemberDTO);

    void registerMemberWithVenture(Member member);

    Member getMemberById(Long memberId);

    Member saveMember(Member member);

}
