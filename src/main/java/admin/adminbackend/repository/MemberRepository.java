package admin.adminbackend.repository;

import admin.adminbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {

}
