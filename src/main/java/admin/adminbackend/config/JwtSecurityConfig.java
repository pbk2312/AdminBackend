package admin.adminbackend.config;


import admin.adminbackend.filter.JwtFilter;
import admin.adminbackend.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // TokenProvider를 주입받아 JwtFilter를 통해 Security 로직에 필터를 등록
    private final TokenGenerator tokenGenerator;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtFilter jwtFilter = new JwtFilter(tokenGenerator);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
