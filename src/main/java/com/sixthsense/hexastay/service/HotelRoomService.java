package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.SettleDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface HotelRoomService {

    //1-1.void todo: member 참조 등록 메서드
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO);

    //2-1
    public Page<HotelRoomDTO> roomMemberPage(Pageable pageable);


    //0411
    public HotelRoomDTO HotelRoomByName(String hotelRoomName);

    //checkInOut 메소드
    public void checkInOut(Long hotelRoomNum,String status);





    //***************단일 호텔룸 CRRUD **************//
    //호텔룸만 등록
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO,Long companyNum) throws IOException;

    //2-2.리스트(pageable) todo: member의 참조 값을 가지고 있는 메서드
    public Page<HotelRoomDTO> hotelroomList(Pageable page);

    //2.3. 검색에서 활용할 service - 검색 기준은 hotelRoomName (호텔 이름)
    public Page<HotelRoomDTO> searchHotelRoomsByName(String keyword, Pageable pageable);

    //읽기
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum);

    //수정
    public void hotelroomUpdate(Long hotelRoomNum, HotelRoomDTO hotelRoomDTO,Long companyNum ) throws IOException;

    //삭제
    public void hotelroomDelet(Long hotelRoomNum);

    //호텔룸에서 컴퍼니 넘에서 가져 오기
    public List<HotelRoom> listCompany(Long companyNUm);

    //******************단일 호텔룸 CRRUD *************//


    // 정산전용
    List<HotelRoomDTO> getSettleList(Long companyNum);
    Page<HotelRoomDTO> getSettleList(Long companyNum, Pageable pageable);

    List<SettleDTO> getSettleListByDateRange(LocalDate startDate, LocalDate endDate);
}
