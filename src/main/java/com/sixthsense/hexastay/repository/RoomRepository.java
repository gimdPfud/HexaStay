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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface RoomRepository extends JpaRepository<Room,Long> {

    //roomPassword 만 찾아오는 커리 메소드
    Optional<Room> findRoomByRoomPassword(String roomPassword);

    Page<Room> findByHotelRoom_HotelRoomNum(Long hotelRoomNum, Pageable pageable);

    Page<Room> findByMember_MemberNum(Long memberNum, Pageable pageable);

    @EntityGraph(attributePaths = {"hotelRoom", "member"})
    Page<Room> findAll(Pageable pageable);


    // hotelRoomNum 기준으로 연결된 member 리스트로 가져오는 repository  가져오기
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomNum = :hotelRoomNum")
    List<Room> findByHotelRoomNum(@Param("hotelRoomNum") Long hotelRoomNum);

    // hotelRoomNum 기준으로 연결된 member들만 가져오기
    @Query("SELECT r.member FROM Room r WHERE r.hotelRoom.hotelRoomNum = :hotelRoomNum")
    Optional<Member> findMemberByHotelRoomNum(@Param("hotelRoomNum") Long hotelRoomNum);


    //hotelRoomStatus 즉 value = checkin , value = checkout 상태에 따라 보여주는 Repository
    @Query("SELECT r FROM Room r WHERE r.hotelRoom.hotelRoomStatus = :status")
    Page<Room> findByHotelRoomStatus(@Param("status") String status, Pageable pageable);





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




    // 특정 Member ID를 가진 HotelRoom 조회 (해당 멤버가 배정된 호텔룸)
//    List<Room> findByMember_MemberId(Long memberId);






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

    // 체크인, 체크아웃에 따라 장바구니 로직에 추가할 것을 감별
    Optional<Room> findByMemberAndCheckOutDateIsNull(Member member);
}
