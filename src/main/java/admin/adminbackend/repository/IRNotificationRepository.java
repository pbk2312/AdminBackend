package admin.adminbackend.repository;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRNotificationRepository extends JpaRepository<IRNotification, Long> {

    List<IRNotification> findByceo(Member ceo); // 메서드 이름을 변경
    List<IRNotification> findByMember(Member member);

}
