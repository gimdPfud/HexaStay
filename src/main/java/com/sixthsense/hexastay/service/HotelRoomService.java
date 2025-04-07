package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;

import com.sixthsense.hexastay.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HotelRoomService {

    public void insertMemberHoterl(HotelRoomDTO hotelRoomDTO);

    //3.hotel룸 읽기 페이지 member 에 정보를 가져 오는 페이지
    public HotelRoomDTO findRoomWithMembers(Long roomNum);



    //todo기본 로직 만 만들었음 - 참조 pk 가져 오실분은 등록만 수정 보시면 됩니다.
    public Optional<MemberDTO> getMemberByHotelRoomNum(Long hotelRoomNum);


    //member 참조 pk를 가져 오는 호텔룸 등록 페이지
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO, Long memberNum);

    // 호텔룸과 배정된 회원 정보 조회
    public Optional<HotelRoomDTO> getHotelRoomWithMember(Long hotelRoomNum);


    //Member의 정보를 가지고 있는 호텔룸 정보 가져 오기
    public List<MemberDTO> getAllMembersInHotelRooms();


    //호텔룸만 등록
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO);

    //리스트
    public Page<HotelRoomDTO> hotelroomList(Pageable page);

    //읽기
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum);

    //수정
    public void hotelroomrModify(HotelRoomDTO hotelRoomDTO);

    //삭제
    public void hotelroomDelet(Long hotelRoomNum);


}
