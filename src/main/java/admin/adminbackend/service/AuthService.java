package admin.adminbackend.service;


import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.RefreshTokenRepository;
import admin.adminbackend.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    private final RefreshTokenRepository repository;

    @Transactional
    private




}
