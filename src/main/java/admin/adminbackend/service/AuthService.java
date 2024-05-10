package admin.adminbackend.service;


import admin.adminbackend.domain.EmailCertification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.RefreshToken;
import admin.adminbackend.domain.ResetToken;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.dto.token.TokenRequestDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.repository.EmailRepository;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.RefreshTokenRepository;
import admin.adminbackend.jwt.TokenProvider;
import admin.adminbackend.repository.ResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailProvider emailProvider;
    private final EmailRepository emailRepository;
    private final ResetTokenRepository resetTokenRepository;

    // 회원가입

    @Transactional
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


    // 탈퇴하기
    @Transactional
    public MemberResponseDTO withdrawalMembership(LoginDTO loginDTO) {
        String withdrawalMembershipEmail = loginDTO.getEmail();
        String withdrawalMembershipPassword = loginDTO.getPassword();

        // 해당 이메일로 회원을 찾습니다.
        Member member = memberRepository.findByEmail(withdrawalMembershipEmail)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 회원의 비밀번호를 확인합니다.
        if (!passwordEncoder.matches(withdrawalMembershipPassword, member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 해당 회원의 RefreshToken을 삭제합니다.
        refreshTokenRepository.deleteByEmail(withdrawalMembershipEmail);

        // 회원을 삭제합니다.
        memberRepository.delete(member);

        log.info("회원탈퇴가 완료되었습니다.");

        return MemberResponseDTO.of(member);
    }


    // 로그인 시도
    @Transactional
    public TokenDTO login(LoginDTO loginDTO) {
        log.info("로그인 시도: 사용자 아이디={}", loginDTO.getEmail());

        // 1. 로그인 ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDTO.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailService 에서 만들었던 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("사용자 인증 완료: 사용자 아이디={}", authentication.getName());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        log.info("JWT 토큰 생성 완료");

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .email(loginDTO.getEmail())
                .key(authentication.getName())
                .value(tokenDTO.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken 저장 완료: 사용자 아이디={}", authentication.getName());

        // 5. 토큰 발급
        log.info("로그인 완료: 사용자 아이디={}", authentication.getName());
        return tokenDTO;
    }


    // 로그아웃
    @Transactional
    public LogoutDTO logout(LogoutDTO logoutDTO) {
        log.info("로그아웃을 시도합니다...");

        String logoutEmail = logoutDTO.getEmail();

        Optional<RefreshToken> findRefreshToeken = refreshTokenRepository.findByEmail(logoutEmail);

        // RefreshToken이 존재하면 삭제
        findRefreshToeken.ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            log.info("RefreshToken 삭제 완료: 사용자 이메일={}", logoutEmail);
        });

        log.info("로그아웃이 완료되었습니다.");


        return logoutDTO;
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



    // 비밀번호 찾기
    public EmailResponseDTO sendPasswordResetEmail(EmailRequestDTO emailRequestDTO) {

        // 해당 이메일로 회원을 찾습니다.
        Member member = memberRepository.findByEmail(emailRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

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
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }

        return EmailResponseDTO.change(emailRequestDTO);
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

    // 새로운 AccessToken 과 RefreshToken 발급

    @Transactional
    public TokenDTO reissuance(TokenRequestDTO tokenRequestDTO) {

        // 1. RefreshToken 유효한지 검증
        if (!tokenProvider.validate(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효 X");
        }

        // 2. Access token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDTO.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자"));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDTO.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDTO;

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