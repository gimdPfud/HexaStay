package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomServiceImpl {

    private final HotelRoomRepository hotelRoomRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Transactional
    public void insertHotelRoomAndAssignMember(HotelRoomDTO hotelRoomDTO) {
        // 호텔룸 저장
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        log.info("호텔룸 저장 완료 - 번호: {}", hotelRoom.getHotelRoomNum());

        // 회원 정보 가져오기
        Member member = memberRepository.findById(hotelRoomDTO.getMemberNum())
                .orElseThrow(EntityNotFoundException::new);
//                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
        hotelRoom.setMember(member);
        hotelRoom = hotelRoomRepository.save(hotelRoom);
        // Room 엔티티 저장 (호텔룸과 회원 연결)
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .build();
        roomRepository.save(room);
        log.info("Room 엔티티 저장 완료 - 호텔룸: {}, 회원: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());
    }

    //RoomDB 에서 hotelroom FK을 따라가서 member에 정보를 가져 오는 서비스 메서드
    public RoomDTO getRoomByHotelRoomNum(Long hotelRoomNum) {
        Room room = roomRepository.findByHotelRoom_HotelRoomNum(hotelRoomNum)
                .orElseThrow(() -> new RuntimeException("해당 호텔룸에 대한 Room 정보가 없습니다."));

        return modelMapper.map(room, RoomDTO.class);
    }

    public RoomDTO getRoomInfoByHotelRoomId(Long roomId) {
        log.info("Room 정보 조회 - HotelRoom ID: {}", roomId);

        Room room = roomRepository.findByHotelRoom_HotelRoomNum(roomId)
                .orElseThrow(() -> new RuntimeException("해당 방 정보를 찾을 수 없습니다."));

        return modelMapper.map(room, RoomDTO.class);
    }

    //member id을 기준으로 룸의 정보를 가져 오는 메서드
//    public List<HotelRoomDTO> getHotelRoomsByMemberId(Long memberId) {
//        List<Room> rooms = roomRepository.findByMember_MemberId(memberId);
//        return rooms.stream()
//                .map(room -> modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class))
//                .collect(Collectors.toList());
//    }



    // (추가) 호텔룸 리스트 조회
    public List<HotelRoomDTO> getAllHotelRooms() {
        List<HotelRoom> hotelRooms = hotelRoomRepository.findAll();
        return hotelRooms.stream()
                .map(hotelRoom -> modelMapper.map(hotelRoom, HotelRoomDTO.class))
                .collect(Collectors.toList());
    }
}
