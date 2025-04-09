package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RoomRepository extends JpaRepository<Room,Long> {

    Optional<Room> findByHotelRoom_HotelRoomNum(Long hotelRoomNum);

    Page<Room> findByHotelRoom_HotelRoomNum(Long hotelRoomNum, Pageable pageable);

    Page<Room> findByMember_MemberNum(Long memberNum, Pageable pageable);

    Page<Room> findAll(Pageable pageable);

    // 특정 Member ID를 가진 HotelRoom 조회 (해당 멤버가 배정된 호텔룸)
//    List<Room> findByMember_MemberId(Long memberId);



}
