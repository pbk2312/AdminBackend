package admin.adminbackend.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Log4j2
@EnableWebMvc // HTTP 요청을 처리하는데 사용
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("logIn Check....");

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());// CSRF 토큰 비활성화
        return http.build();
    }


}
