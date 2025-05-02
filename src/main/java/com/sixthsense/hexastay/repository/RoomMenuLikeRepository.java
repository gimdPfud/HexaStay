package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/***********************************************
 * 클래스명 : RoomMenuLikeRepository
 * 기능 : 룸 메뉴 좋아요 관련 데이터베이스 접근을 위한 JPA Repository 인터페이스
 * - RoomMenuLike 엔티티에 대한 기본적인 CRUD 연산 제공
 * - 특정 회원 이메일과 룸 메뉴를 기반으로 좋아요 정보를 조회하는 기능 제공 (Optional)
 * - 특정 룸 메뉴의 좋아요 또는 싫어요 상태에 따른 총 개수를 조회하는 기능 제공
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-08
 * 수정일 : -
 * ***********************************************/

public interface RoomMenuLikeRepository extends JpaRepository<RoomMenuLike, Long> {

    Optional<RoomMenuLike> findByMember_MemberEmailAndRoomMenu(String memberEmail, RoomMenu roomMenu);

    Long countByRoomMenuAndRoomMenuLikedCheck(RoomMenu roomMenu, Boolean likedCheck);

    // 연결된 좋아요 삭제
    void deleteByRoomMenu_RoomMenuNum(Long roomMenuNum);

    // 좋아요 삭제하기

}
