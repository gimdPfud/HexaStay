package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface RoomRepository extends JpaRepository<Room,Long> {

    //roomPassword 만 찾아오는 커리 메소드
    Optional<Room> findRoomByRoomPassword(String roomPassword);

    Page<Room> findByHotelRoom_HotelRoomNum(Long hotelRoomNum, Pageable pageable);

    Page<Room> findByMember_MemberNum(Long memberNum, Pageable pageable);

    @EntityGraph(attributePaths = {"hotelRoom", "member"})
    Page<Room> findAll(Pageable pageable);

    // 특정 Member ID를 가진 HotelRoom 조회 (해당 멤버가 배정된 호텔룸)
//    List<Room> findByMember_MemberId(Long memberId);






    // 정산용
    @EntityGraph(attributePaths = {"hotelRoom", "member"})
    Page<Room> findByHotelRoom_HotelRoomNumIn(List<Long> hotelRoomNums, Pageable pageable);

}
