package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // 멤버를 참조하여 이메일을 찾는다.
    public Page<RoomMenuOrder> findByMember_MemberEmail(String email, Pageable pageable);

    List<RoomMenuOrder> findByMemberOrderByRegDateDesc(Member member);

    // 오더된 정보만 빼오기.
    List<RoomMenuOrder> findAllByRoomMenuOrderStatusOrderByRegDateDesc(RoomMenuOrderStatus status);


}
