package admin.adminbackend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
public class JwtFilter extends OncePerRequestFilter {


    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString(); // 요청 ID 생성
        long startTime = System.currentTimeMillis(); // 요청 시작 시간

        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 시작", requestId, request.getRequestURI());


        // Request Header와 쿠키에서 토큰을 꺼낸다
        String jwt = resolveToken(request);

        if (jwt == null) {
            log.info("Request ID: {} - 요청 헤더와 쿠키에 JWT 토큰이 없음", requestId);
        } else {
            log.info("Request ID: {} - JWT 토큰 발견: {}", requestId, jwt);
        }

        // validate로 토큰 유효성을 검사
        // 정상 토큰이면 해당 토큰으로 Authentication을 가져와 SecurityContext에 저장
        if (StringUtils.hasText(jwt) && tokenProvider.validate(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Request ID: {} - 유효한 JWT 토큰. 인증 정보 설정: {}", requestId, authentication.getName());
        } else {
            if (StringUtils.hasText(jwt)) {
                log.info("Request ID: {} - 유효하지 않은 JWT 토큰", requestId);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime; // 요청 처리 시간
        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 종료. 처리 시간: {} ms", requestId, request.getRequestURI(), duration);
    }

    // 요청 헤더와 쿠키에서 토큰 정보 꺼내기
    private String resolveToken(HttpServletRequest request) {
        // 헤더에서 토큰 정보 꺼내기
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }

        // 쿠키에서 토큰 정보 꺼내기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
