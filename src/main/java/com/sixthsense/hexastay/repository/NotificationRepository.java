package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 읽지 않은 알림 개수 조회 (모든 관리자 대상)
    long countByIsReadFalse();

    // findByIsReadFalseOrderByCreatedAtDesc(); // 기존 이름
    List<Notification> findByIsReadFalseOrderByCreateDateDesc(); // 수정된 이름

    // 특정 알림들을 읽음 처리 (ID 목록 사용)

    @Modifying // 데이터 변경 쿼리임을 명시
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId IN :ids AND n.isRead = false")
    int markAsReadByIds(@Param("ids") List<Long> ids);
}
