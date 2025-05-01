package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/***********************************************
 * 클래스명 : RoomMenuOrderRepository
 * 기능 : 룸 메뉴 주문 관련 데이터베이스 접근을 위한 JPA Repository 인터페이스
 * - RoomMenuOrder 엔티티에 대한 기본적인 CRUD 연산 제공
 * - 특정 회원의 이메일을 기반으로 주문 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 회원의 주문 목록을 등록일자 내림차순으로 조회하는 기능 제공
 * - 특정 주문 상태에 해당하는 모든 주문 목록을 등록일자 내림차순으로 조회하는 기능 제공
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-10
 * 수정일 : -
 * ***********************************************/

public interface RoomMenuOrderRepository extends JpaRepository<RoomMenuOrder, Long>{

    // 페이징을 위한 메서드: Pageable 인자를 추가하면 Page<RoomMenuOrder> 반환
    // FetchType.EAGER 처럼 필요한 연관 엔티티를 함께 조회하도록 설정
    @EntityGraph(attributePaths = {"member", "hotelRoom", "orderItems", "orderItems.roomMenu"})
    Page<RoomMenuOrder> findAllByRoomMenuOrderStatusInOrderByRegDateDesc(Collection<RoomMenuOrderStatus> statuses, Pageable pageable);


    // 기존 메서드: List<RoomMenuOrder> findByMemberOrderByRegDateDesc(Member member);
    // 페이징을 위해 Pageable 인자를 추가하여 Page를 반환하도록 수정
    Page<RoomMenuOrder> findByMemberOrderByRegDateDesc(Member member, Pageable pageable);


}

