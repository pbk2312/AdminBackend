package admin.adminbackend.service;


import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.myPage.IRNotificationDTO;
import admin.adminbackend.repository.IRNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class IRService {

    private final IRNotificationRepository irNotificationRepository;
    @Transactional
    public boolean IRSend(Member ceo, Member member) {
        try {
            // IRNotification 생성 및 저장 로직
            IRNotification notification = new IRNotification();
            notification.setCeo(ceo);
            notification.setMember(member);
            notification.setRead(false); // 기본적으로 읽지 않은 상태로 설정
            // Save notification to the repository
            irNotificationRepository.save(notification);
            return true; // 성공
        } catch (Exception e) {
            // 예외 처리 및 로그 기록
            e.printStackTrace();
            return false; // 실패
        }
    }

    @Transactional(readOnly = true)
    public List<IRNotificationDTO> findIRList(Member ceo) {
        if (ceo == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        log.info("Finding IR notifications for member: {}", ceo);

        // IR 알림 리스트를 조회
        List<IRNotification> irList = irNotificationRepository.findByceoAndIsReadFalse(ceo);
        log.info("Found {} IR notifications", irList.size());

        // DTO로 변환
        return irList.stream().map(ir -> {
            Member ventureMember = ir.getCeo();
            Member personMember = ir.getMember();
            return new IRNotificationDTO(
                    ir.getId(),
                    ventureMember != null ? ventureMember.getId() : null,
                    ventureMember != null ? ventureMember.getEmail() : null,
                    personMember != null ? personMember.getId() : null,
                    personMember != null ? personMember.getEmail() : null,
                    ir.isRead(),
                    ir.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }


    @Transactional
    public IRNotification findIRSendMember(Long IRNotificationId){
        IRNotification irNotification = irNotificationRepository.findById(IRNotificationId).orElseThrow(() -> new RuntimeException("IR이 없다"));
        return irNotification;
    }

    // IRNotification 저장 메서드
    @Transactional
    public void saveIRNotification(IRNotification irNotification) {
        irNotificationRepository.save(irNotification);
    }
}
