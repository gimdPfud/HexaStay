package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelRoom {

    //1.등록
    public void insertHotelRoom(HotelRoomDTO hotelRoomDTO);

    //2.리스트 - Pageable 포함 페이지 처리 포함
    public Page<HotelRoomDTO> listHotelRoom(Pageable pageable,String keyword);

    //3.삭제
    public void delete(Long roomnum);


    //4.상세보기
    public HotelRoomDTO readHotelRoome(Long roomnum);

    //5.수정
    public void modifyHotelRoom(HotelRoomDTO hotelRoomDTO);


}
