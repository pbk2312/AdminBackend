package admin.adminbackend.service;


import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.RefreshToken;
import admin.adminbackend.dto.MemberReqeustDTO;
import admin.adminbackend.dto.MemberResponseDTO;
import admin.adminbackend.dto.TokenDTO;
import admin.adminbackend.dto.TokenRequestDTO;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.RefreshTokenRepository;
import admin.adminbackend.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private TokenGenerator tokenGenerator;
    private final RefreshTokenRepository refreshTokenrepository;

    @Transactional
    public MemberResponseDTO regiter(MemberReqeustDTO reqeustDTO) {
        if (memberRepository.existsByEmail(reqeustDTO.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 회원입니다");
        }

        Member member = reqeustDTO.toMember(passwordEncoder);
        return MemberResponseDTO.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDTO login(MemberReqeustDTO memberReqeustDTO) {
        // 1. 로그인 ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authentication = memberReqeustDTO.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될때 CustomUserDetailService 에서 만들었던 loadUserByUsername 메서드 실행
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDTO tokenDTO = tokenGenerator.generateTokenDto(authentication);

        // 4.RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDTO.getRefreshToken())
                .build();

        refreshTokenrepository.save(refreshToken);

        //5. 토큰 발급
        return tokenDTO;

    }

    // 새로운 AccessToken 과 RefreshToken 발급
    @Transactional
    public TokenDTO reissuance(TokenRequestDTO tokenRequestDTO) {

        // 1. RefreshToken 유효한지 검증
        if (!tokenGenerator.validate(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효 X");
        }

        // 2. Access token 에서 Member ID 가져오기
        Authentication authentication = tokenGenerator.getAuthentication(tokenRequestDTO.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenrepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자"));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDTO = tokenGenerator.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDTO.getRefreshToken());

        return tokenDTO;

    }


}
