package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;

import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.HotelRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface HotelRoomService {

    //1-1.void todo: member 참조 등록 메서드
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO);

    //2-1
    public Page<HotelRoomDTO> roomMemberPage(Pageable pageable);


    //0411
    public HotelRoomDTO getLatestHotelRoomByType(String roomType);



    //***************단일 호텔룸 CRRUD **************//
    //호텔룸만 등록
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO) throws IOException;

    //2-2.리스트(pageable) todo: member의 참조 값을 가지고 있는 메서드
    public Page<HotelRoomDTO> hotelroomList(Pageable page);

    //읽기
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum);

    //수정
    public void hotelroomrModify(HotelRoomDTO hotelRoomDTO);

    //삭제
    public void hotelroomDelet(Long hotelRoomNum);

    //호텔룸에서 컴퍼니 넘에서 가져 오기
    public List<HotelRoom> listCompany(Long companyNUm);

    //******************단일 호텔룸 CRRUD *************//


}
