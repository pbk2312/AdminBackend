package admin.adminbackend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {


    private SecurityUtil() {

    }

    // SecurityContext에 유저 정보가 저장되는 시점
    // Request 가 들어올때 JwtFilter 의 dofilter에서 저장
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 안에 인증 정보 X");
        }

        return Long.parseLong(authentication.getName());

    }

}
