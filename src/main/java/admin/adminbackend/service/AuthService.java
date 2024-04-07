package admin.adminbackend.service;


import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.RefreshToken;
import admin.adminbackend.dto.MemberReqeustDTO;
import admin.adminbackend.dto.MemberResponseDTO;
import admin.adminbackend.dto.TokenDTO;
import admin.adminbackend.dto.TokenRequestDTO;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.RefreshTokenRepository;
import admin.adminbackend.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponseDTO register(MemberReqeustDTO memberReqeustDTO) {
        if (memberRepository.existsByEmail(memberReqeustDTO.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 회원입니다");
        }

        Member member = memberReqeustDTO.toMember(passwordEncoder);
        return MemberResponseDTO.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDTO login(MemberReqeustDTO memberRequestDTO) {
        log.info("로그인 시도: 사용자 아이디={}", memberRequestDTO.getEmail());

        // 1. 로그인 ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDTO.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailService 에서 만들었던 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("사용자 인증 완료: 사용자 아이디={}", authentication.getName());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        log.info("JWT 토큰 생성 완료");

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDTO.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken 저장 완료: 사용자 아이디={}", authentication.getName());

        // 5. 토큰 발급
        log.info("로그인 완료: 사용자 아이디={}", authentication.getName());
        return tokenDTO;
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


}
