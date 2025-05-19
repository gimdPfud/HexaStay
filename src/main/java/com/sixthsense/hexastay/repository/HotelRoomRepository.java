/***********************************************
 * 인터페이스명 : HotelRoomRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.HotelRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {


    //정렬 조건 추가 레퍼지토리
    @Query("SELECT h FROM HotelRoom h")
    Page<HotelRoom> findAllHotelRooms(Pageable pageable);


    //리스트로 호텔이름을 찾아 오는 Query DSL 정렬 기준은 hotelRoomNum
    @Query("SELECT h FROM HotelRoom h WHERE h.hotelRoomName LIKE %:keyword% ORDER BY h.hotelRoomNum DESC")
    List<HotelRoom> searchByName(@Param("keyword") String keyword);


    //호텔 방이름을 기준으로 하는 검색 조건
    @Query("SELECT hr FROM HotelRoom hr WHERE hr.hotelRoomName = :hotelRoomName ORDER BY hr.hotelRoomNum DESC")
    Optional<HotelRoom> findByHotelRoomName(@Param("hotelRoomName") String hotelRoomName);


    //호텔룸에 참조되어 있는 ComplnyNum 정보를 리스트로 가져오기
    List<HotelRoom> findByCompany_CompanyNum(Long companyNum);
    Page<HotelRoom> findByCompany_CompanyNum(Long companyNum, Pageable pageable);

    List<HotelRoom> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);








}
