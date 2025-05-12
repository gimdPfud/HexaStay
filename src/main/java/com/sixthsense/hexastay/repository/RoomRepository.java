package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //roomPassword 만 찾아와서 유효성 검사
    //2.Long 타입 - 비밀번호 중복체크 및 비번 추천
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomPassword = :roomPassword")
    long countByRoomPassword(@Param("roomPassword") String roomPassword);

    //todo : room 상태 컬럼 추가해서 상태에 따라 보여지고 안보여지는 컬럼 추가
    //
    @Query("SELECT r FROM Room r WHERE (r.roomDisplayStatus IS NULL OR r.roomDisplayStatus = :status)")
    Page<Room> findByRoomDisplayStatus(@Param("status") String status, Pageable pageable);

    //추가 검색용 레포지토리 링크별로
    //1.검색
    @Query("SELECT r FROM Room r " +
            "WHERE r.hotelRoom.company.companyNum = :companyNum " +
            "AND r.hotelRoom.hotelRoomStatus = :status " +
            "AND (r.member.memberName LIKE %:keyword% OR r.member.memberEmail LIKE %:keyword%)")
    Page<Room> findByCompanyAndStatusAndKeyword(@Param("companyNum") Long companyNum,
                                                @Param("status") String status,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    //2.검색
    @Query("SELECT r FROM Room r WHERE " +
            "(r.roomDisplayStatus = :status OR r.roomDisplayStatus IS NULL) " +
            "AND r.hotelRoom.company.companyNum = :companyNum " +
            "AND (r.member.memberName LIKE %:keyword% OR r.member.memberEmail LIKE %:keyword%)")
    Page<Room> findByDisplayStatusAndKeyword(@Param("companyNum") Long companyNum,
                                             @Param("status") String status,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);


    //3.검색
    @Query("SELECT r FROM Room r " +
            "WHERE r.hotelRoom.company.companyNum = :companyNum " +
            "AND (r.member.memberName LIKE %:keyword% OR r.member.memberEmail LIKE %:keyword%)")
    Page<Room> findByCompanyAndKeyword(@Param("companyNum") Long companyNum,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);



    //todo:@GetMapping("/membersByHotelRoom/{hotelRoomNum}") - RoomController
    //roomlist 에서 member 클릭시 유저가 예약한 호텔룸 정보
    Page<Room> findByHotelRoom_HotelRoomNum(Long hotelRoomNum, Pageable pageable);

    //todo:@GetMapping("/hotelRoomsByMember/{memberNum}") -- RoomController
    //roomlist 에서 hotelRoom 클릭시 호텔룸에 예약된 유저의 정보 보기
    Page<Room> findByMember_MemberNum(Long memberNum, Pageable pageable);



    // hotelRoomNum 기준으로 연결된 member 리스트로 가져오는 repository  가져오기
    //todo : 사용중
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomNum = :hotelRoomNum")
    List<Room> findByHotelRoomNum(@Param("hotelRoomNum") Long hotelRoomNum);

    //todo : 검색용 roomlist.html
    //member 의 이름과  email로 찾아 오는 조건
    @Query("SELECT r FROM Room r WHERE " +
            "LOWER(r.member.memberName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.member.memberEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Room> searchRoomsByMemberNameOrEmailPaged(@Param("keyword") String keyword, Pageable pageable);


    //todo:나중에 필요해서 만들었던 로직 지금은 사용 안함
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomNum = :hotelRoomNum AND r.hotelRoom.hotelRoomStatus = 'checkin'")
    List<Room> findCheckinRoomsByHotelRoomNum(@Param("hotelRoomNum") Long hotelRoomNum);


    //hotelRoomStatus 즉 value = checkin , value = checkout 상태에 따라 보여주는 Repository
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomStatus = :status")
    Page<Room> findByHotelRoomStatus(@Param("status") String status, Pageable pageable);

    //qr/{hotelRoomNum}들어 갈때 쓰는 로직 - hotelroom status = 'checkin' 상태에서만 패스워드로 접속 가능
    @Query("SELECT r FROM Room r WHERE r.roomPassword = :roomPassword AND r.hotelRoom.hotelRoomStatus = 'checkin'")
    Optional<Room> findCheckinRoomByPassword(@Param("roomPassword") String roomPassword);


    //Room pk 을 찾아와서 member fk 만 변경 하는 메소드
    //todo:membersByHotelRoom.html
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.member.memberNum = :memberNum WHERE r.roomNum = :roomNum")
    void updateRoomMember(@Param("roomNum") Long roomNum, @Param("memberNum") Long memberNum);

    //Room Pk 을 찾아와서 HotelRoom Fk 변경하는 메소드
    //todo:hotelRoomsByMember.html
    @Modifying
    @Query("UPDATE Room r SET r.hotelRoom.hotelRoomNum = :hotelRoomNum WHERE r.roomNum = :roomNum")
    void updateHotelRoomFk(@Param("roomNum") Long roomNum, @Param("newHotelRoomNum") Long hotelRoomNum);

    /****추후에 쓸 메소드****/
    //최신순으로 정렬 하는 repository리 로직
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomNum = :hotelRoomNum ORDER BY r.createDate DESC")
    List<Room> findByHotelRoomNumDesc(@Param("hotelRoomNum") Long hotelRoomNum);


    //member를 조회하고, 시간을 조회하면서, room이 활성화가 되 있는 것을 빼오는 매소드.
    @Query("SELECT r FROM Room r " +
            "WHERE r.member = :member " +
            "AND r.checkInDate IS NOT NULL AND r.checkInDate <= :currentTime " +
            "AND r.checkOutDate IS NOT NULL AND r.checkOutDate > :currentTime")
    Optional<Room> findActiveRoomForMemberAtTime(@Param("member") Member member, @Param("currentTime") LocalDateTime currentTime);

    /****추후에 쓸 메소드****/


    /*****매출 정산용 메소드******/
    // 정산용
    @EntityGraph(attributePaths = {"hotelRoom", "member"})
    Page<Room> findByHotelRoom_HotelRoomNumIn(List<Long> hotelRoomNums, Pageable pageable);

    // 날짜 범위로 정산용 데이터 조회
    @EntityGraph(attributePaths = {"hotelRoom", "member"})
    Page<Room> findByHotelRoom_HotelRoomNumInAndCreateDateBetween(
            List<Long> hotelRoomNums,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    /***윤겸 RoomMenuOrderServiceImpl **/
    // 내림 차순으로 정렬.
    @Query("SELECT r FROM Room r WHERE r.member = :member AND :now BETWEEN r.checkInDate AND r.checkOutDate " +
            "ORDER BY r.roomNum DESC, r.hotelRoom.hotelRoomNum DESC")
    List<Room> findActiveRoomsOrdered(@Param("member") Member member, @Param("now") LocalDateTime now);

    // 직원이 속한 룸 리스트
    Page<Room> findByHotelRoom_Company_CompanyNum(Long companyNum, Pageable pageable);

    // 직속 + 체크인이냐 체크아웃이냐
    Page<Room> findByHotelRoom_Company_CompanyNumAndHotelRoom_HotelRoomStatus(Long companyNum, String hotelStatus, Pageable pageable);
            
    // 페이지네이션 없는 전체 데이터 조회용
    List<Room> findByHotelRoom_HotelRoomNumIn(List<Long> hotelRoomNums);
    
    // 페이지네이션 없는 날짜 범위 데이터 조회용
    List<Room> findByHotelRoom_HotelRoomNumInAndCreateDateBetween(
            List<Long> hotelRoomNums, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 체크아웃 날짜 범위로 조회
    List<Room> findByCheckOutDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 첫 번째 결과만 반환하는 메서드 (findFirst를 사용)
    Optional<Room> findFirstByMemberOrderByCheckOutDateDesc(Member member);
}
