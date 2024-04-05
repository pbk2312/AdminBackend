package admin.adminbackend.security;

import admin.adminbackend.dto.ToeknDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
@Component
public class TokenGenerator {

    private static final String AUTHKEY = "auth"; // 페이로드에 권한 정보를 저장하기 위한 키
    private static final String BEARERTYPE = "Bearer"; // JWT 토큰 타입 나타내는 문자열
    private static final long AccesstokenExpireTime= 1000 * 60 * 60;            // 60분
    private static final long RefreshTokenExpireTime = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final Key key; // 시크릿키 저장

    public TokenGenerator(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public ToeknDTO generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + AccesstokenExpireTime);
        String accessToken = Jwts.builder()
                .claim(AUTHKEY, authorities)        // payload "auth": "Venture"
                .setSubject(authentication.getName())       // payload "sub": "name"
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .setExpiration(accessTokenExpiresIn)        // payload "exp"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + RefreshTokenExpireTime))
                .compact();

        return ToeknDTO.builder()
                .grantType(BEARERTYPE)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHKEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHKEY).toString().split(",")) // 권한 정보를 쉼표로 구분
                        .map(SimpleGrantedAuthority::new) // SimpleGrantedAuthority 객체로 매핑
                        .collect(Collectors.toList());


        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었다");
        }
        return false;
    }

    // 클레임 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
