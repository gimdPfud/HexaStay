package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelRoomService {

    //등록
    //todo기본 로직 만 만들었음 - 참조 pk 가져 오실분은 등록만 수정 보시면 됩니다.
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
