package admin.adminbackend.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Log4j2
@EnableWebMvc // HTTP 요청을 처리하는데 사용
@RequiredArgsConstructor
public class SecurityConfig {


    // 패스워드 인코더 설정
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("logIn Check....");

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());// CSRF 토큰 비활성화

        http.formLogin(form-> form.loginPage("/login")); // 사용자가 인증되지 않은 경우 "/login"으로 리다이렉트
        http.sessionManagement(sesstionManagement -> sesstionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션을 사용하지 않음


        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        log.info("정적 리소스 처리 X");

        return (web)-> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


}
