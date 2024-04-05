package admin.adminbackend.filter;

import admin.adminbackend.security.TokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AuthHeader = "Auth";
    private static final String BeaererPrefix = "Bearer";

    private final TokenGenerator tokenGenerator;


    // Jwt 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Request Header에서 토큰을 꺼낸다
        String jwt = TokenResolve(request);

        // validate로 토큰 유효성을 검사
        // 정상 토큰이면 해당 토큰으로 Authentication을 가져와 SecurityContext에 저장
        if (StringUtils.hasText(jwt) && tokenGenerator.validate(jwt)) {
            Authentication authentication = tokenGenerator.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);


    }

    private String TokenResolve(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BeaererPrefix)) {
            return bearerToken.split(" ")[1].trim();
        }
        return null;
    }

}
