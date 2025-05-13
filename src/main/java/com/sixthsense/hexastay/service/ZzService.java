/***********************************************
 * 클래스명 : ZzService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-24
 * 수정 : 2025-04-24
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ZzService {
//    private final MemberRepository memberRepository;
//    private final RoomServiceImpl roomService;
    private final RoomRepository roomRepository;
    private final HotelRoomRepository hotelRoomRepository;
    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
//    public Long principalToHotelroomNum(Principal principal){
//        String email = principal.getName();
//        Long memberNum = memberRepository.findByMemberEmail(email).getMemberNum();
//        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
//        HotelRoomDTO hotelRoomDTO = roomService.getHotelRoomsByMember(memberNum,pageable).stream().findFirst().orElseThrow(EntityNotFoundException::new);
//        return hotelRoomDTO.getHotelRoomNum();
//    }

//    //hotelroomNum으로 Member찾기
//    public Member hotelroomNumToMember(Long hotelroomNum){
//        Room room = roomService.readRoomByHotelRoomNum(hotelroomNum);
//        return room.getMember();
//    }

    public Company hotelroomNumToCompany(Long hotelroomNum){
        return hotelRoomRepository.findById(hotelroomNum).orElseThrow(EntityNotFoundException::new)
                .getCompany();
    }

    //    //roomNum으로 이것저것 다 하기
    //0. room
    public Room sessionToRoom(HttpSession session){
        Long roomNum = (Long) session.getAttribute("roomNum");
        return roomRepository.findById(roomNum).orElseThrow(EntityNotFoundException::new);
    }

    //1. hotelroomNum 찾기
    public Long sessionToHotelroomNum(HttpSession session){
        return sessionToRoom(session).getHotelRoom().getHotelRoomNum();
    }

    //2. Member 찾기
    public Member sessionToMember(HttpSession session){
        return sessionToRoom(session).getMember();
    }

    //3. Company 찾기
    public Company sessionToCompany(HttpSession session){
        return sessionToRoom(session).getHotelRoom().getCompany();
    }
}
