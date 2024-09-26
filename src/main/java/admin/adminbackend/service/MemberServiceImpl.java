package admin.adminbackend.service;


import admin.adminbackend.domain.EmailCertification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.ResetToken;
import admin.adminbackend.dto.WithdrawalMembershipDTO;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.exception.*;
import admin.adminbackend.repository.EmailRepository;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.jwt.TokenProvider;
import admin.adminbackend.repository.ResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final EmailProvider emailProvider;
    private final EmailRepository emailRepository;
    private final ResetTokenRepository resetTokenRepository;


    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    // 회원가입
    @Transactional
    @Override
    public MemberResponseDTO register(MemberRequestDTO memberRequestDTO) {
        // 이메일로 인증번호 조회
        EmailCertification emailCertification = emailRepository.findByCertificationEmail(memberRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("인증번호를 찾을 수 없습니다."));

        // 사용자가 입력한 인증번호와 DB에 저장된 인증번호를 비교합니다.
        if (!emailCertification.getCertificationNumber().equals(memberRequestDTO.getCertificationNumber())) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // 인증번호가 일치하고, 해당 이메일로 가입된 회원이 있는지 확인
        if (memberRepository.existsByEmail(memberRequestDTO.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 회원입니다");
        }

        // 인증번호 확인 후 회원 가입 진행
        Member member = memberRequestDTO.toMember(passwordEncoder);
        Member savedMember = memberRepository.save(member);


        emailRepository.delete(emailCertification);

        return MemberResponseDTO.of(savedMember);
    }


    @Transactional
    public TokenDTO login(LoginDTO loginDTO) {
        log.info("로그인 시도: 사용자 아이디={}", loginDTO.getEmail());
        Member member = findByEmail(loginDTO.getEmail());
        validatePassword(loginDTO.getPassword(), member.getPassword());

        // 1. 로그인 ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDTO.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("사용자 인증 완료: 사용자 아이디={}", authentication.getName());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        log.info("JWT 토큰 생성 완료");

        // 4. Redis에 RefreshToken 저장
        redisService.setStringValue(String.valueOf(member.getId()), tokenDTO.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        log.info("Redis에 RefreshToken 저장 완료: 사용자 아이디={}", authentication.getName());

        // 5. 토큰 발급
        log.info("로그인 완료: 사용자 아이디={}", authentication.getName());
        return tokenDTO;
    }

    // 로그아웃
    @Transactional
    public void logout(LogoutDTO logoutDTO) {
        log.info("로그아웃 시도: 사용자 아이디={}", logoutDTO.getEmail());

        // 이메일로 멤버 찾기
        Member member = findByEmail(logoutDTO.getEmail());

        // Redis에서 Refresh Token 삭제
        redisService.deleteStringValue(String.valueOf(member.getId()));
        log.info("Redis에서 RefreshToken 삭제 완료: 사용자 아이디={}", logoutDTO.getEmail());
    }

    // 비밀번호 재설정
    @Transactional
    public String memberChangePassword(MemberChangePasswordDTO memberChangePasswordDTO) {
        String email = memberChangePasswordDTO.getEmail();
        String password = memberChangePasswordDTO.getPassword();
        String checkPassword = memberChangePasswordDTO.getCheckPassword();

        if (!password.equals(checkPassword)) {
            throw new RuntimeException("입력한 비밀번호가 일치하지 않습니다.");
        }

        //비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(password);

        memberRepository.updatePasswordByEmail(email, hashedPassword);

        return "비밀번호 변경이 완료되었습니다.";
    }

    public String sendPasswordResetEmail(EmailRequestDTO emailRequestDTO) {

        // 회원 이메일 존재 여부 확인
        memberRepository.findByEmail(emailRequestDTO.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("존재하지 않는 회원입니다."));

        // 임시 비밀번호 생성
        String resetToken = generateResetToken();

        // ResetToken 엔티티 생성 및 저장
        ResetToken tokenEntity = new ResetToken();
        tokenEntity.setEmail(emailRequestDTO.getEmail());
        tokenEntity.setResetToken(resetToken);
        resetTokenRepository.save(tokenEntity);

        // 비밀번호 재설정 링크
        String resetLink = "http://localhost:8080/member/updatePassword?token=" + resetToken + "&email=" + emailRequestDTO.getEmail();

        // 이메일 보내기
        boolean emailSent = emailProvider.sendPasswordResetEmail(emailRequestDTO, resetLink);
        if (!emailSent) {
            throw new SpecificException("이메일 발송에 실패했습니다.");
        }

        return "비밀번호 재설정 이메일 전송 성공";
    }

    public Member findByRefreshToken(String refreshToken) {
        // Redis에서 refreshToken으로 memberId 찾기
        String memberId = redisService.findMemberIdByRefreshToken(refreshToken);
        if (memberId != null) {
            // memberId로 Member 정보 조회
            return memberRepository.findById(Long.valueOf(memberId))
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));
        }
        throw new IllegalArgumentException("유효하지 않은 refreshToken입니다.");
    }


    @Transactional
    public EmailResponseDTO sendCertificationMail(EmailRequestDTO emailRequestDTO) {

        // 회원가입 이메일 보내기
        String certificationNumber = generateCertificationNumber(); // 인증번호 생성
        boolean emailSent = emailProvider.sendCertificationMail(emailRequestDTO.getEmail(), certificationNumber);
        if (!emailSent) {
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }

        EmailCertification email = emailRequestDTO.toEmail(certificationNumber);
        EmailCertification saveEmail = emailRepository.save(email);

        return EmailResponseDTO.of(saveEmail);

    }

    @Override
    public Member getUserDetails(String accessToken) {
        if (isInvalidToken(accessToken)) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return findByEmail(userDetails.getUsername());
    }

    // 회원 찾기
    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("이메일을 찾을 수 없습니다."));

    }

    @Override
    @Transactional
    public String deleteAccount(WithdrawalMembershipDTO withdrawalMembershipDTO) {
        Member member = memberRepository.findByEmail(withdrawalMembershipDTO.getEmail()).orElseThrow(() -> new RuntimeException("존재하지 않는 회원 입니다."));
        validatePassword(withdrawalMembershipDTO.getPassword(), member.getPassword());
        // 해당 회원의 RefreshToken을 삭제합니다.

        memberRepository.delete(member);

        log.info("회원 정보 삭제...");


        return "회원 정보가 정상적으로 삭제되었습니다.";

    }


    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }


    private boolean isInvalidToken(String accessToken) {
        return accessToken == null || !tokenProvider.validate(accessToken);
    }

    // 난수 생성 메서드

    private String generateCertificationNumber() {
        int length = 6; // 인증번호 길이
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 0부터 9까지의 난수 생성
            sb.append(digit);
        }
        return sb.toString();
    }

    // 임시 비밀번호 생성 메서드
    private String generateResetToken() {
        int length = 20; // 임시 비밀번호 길이
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a'); // 알파벳 소문자 랜덤 생성
            sb.append(randomChar);
        }
        return sb.toString();
    }

}