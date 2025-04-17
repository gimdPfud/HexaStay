package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/***********************************************
 * 클래스명 : RoomMenuCartRepository
 * 기능 : 룸 메뉴 장바구니 관련 데이터베이스 접근을 위한 JPA Repository 인터페이스
 * - RoomMenuCart 엔티티에 대한 기본적인 CRUD 연산 제공
 * - 모든 장바구니를 페이징 처리하여 조회하는 기능 제공
 * - 장바구니 번호로 특정 장바구니를 조회하는 기능 제공
 * - 회원 번호로 해당 회원의 장바구니를 조회하는 기능 제공 (Optional)
 * - 회원 이메일로 해당 회원의 장바구니를 조회하는 기능 제공
 * - Member 엔티티를 기반으로 해당 회원의 장바구니를 조회하는 기능 제공 (Optional)
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-08
 * 수정일 : -
 * ***********************************************/

@Repository
public interface RoomMenuCartRepository extends JpaRepository<RoomMenuCart, Long> {
    @Query("select r from RoomMenuCart r")
    public Page<RoomMenuCart> findAll(Pageable pageable);

    // 장바구니 번호로 장바구니 조회
    RoomMenuCart findByRoomMenuCartNum(Long roomMenuCartNum);

    // 유저찾기
    Optional<RoomMenuCart> findByMember_MemberNum(Long memberNum);

    public RoomMenuCart findByMember_MemberEmail(String email);

    // RoomMenuCartRepository
    Optional<RoomMenuCart> findByMember(Member member);


}
