package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;

import com.sixthsense.hexastay.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HotelRoomService {

    //1-1.void todo: member 참조 등록 메서드
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO);

    //2-1
    public Page<HotelRoomDTO> roomMemberPage(Pageable pageable);


    //2-2.리스트(pageable) todo: member의 참조 값을 가지고 있는 메서드
    public Page<HotelRoomDTO> hotelroomList(Pageable page);

    //3-1.hotel룸 읽기 페이지 member 에 정보를 가져 오는 페이지
    public HotelRoomDTO findRoomWithMembers(Long roomNum);



    //A. optional 타입 - 참조 member가져와서 등록 가능 메서드
    public Optional<HotelRoomDTO> getHotelRoomWithMember(Long hotelRoomNum);




    //B.member 호텔룸 등록 (회원 정보 포함) - Return 타입의 등록 메서드 -Long 타입을 받는 메서드
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO, Long memberNum);


    //C.Member의 정보를 가지고 있는 호텔룸 정보 가져 오기 - List타입
    public List<MemberDTO> getAllMembersInHotelRooms();



    //***************단일 호텔룸 CRRUD **************//
    //호텔룸만 등록
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO);



    //읽기
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum);

    //수정
    public void hotelroomrModify(HotelRoomDTO hotelRoomDTO);

    //삭제
    public void hotelroomDelet(Long hotelRoomNum);

    //******************단일 호텔룸 CRRUD *************//


}
