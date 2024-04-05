package admin.adminbackend.config;

import admin.adminbackend.filter.handler.JwtAccessDeniedHandler;
import admin.adminbackend.filter.handler.JwtAuthFailHandler;
import admin.adminbackend.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenGenerator tokenGenerator;
    private final JwtAuthFailHandler jwtAuthFailHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthFailHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/member/**").permitAll()
                        .anyRequest().authenticated()
                )
                .with(new JwtSecurityConfig(tokenGenerator), jwtSecurityConfig -> {
                    // JwtSecurityConfig에 대한 커스터마이즈 작업을 수행합니다.
                });

        return http.build();

    }
}
