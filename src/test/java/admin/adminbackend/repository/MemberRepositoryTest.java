package admin.adminbackend.repository;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Role;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testInserts() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder()
                    .name("member" + i)
                    .password(passwordEncoder.encode("1111"))
                    .email("asdawda@naver.com" + i)
                    .role(Role.Venture)
                    .build();
            memberRepository.save(member);
        });


    }


}