package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomServiceImpl {
    //todo: DB 명 _ Room

    private final HotelRoomRepository hotelRoomRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    //todo: [[Member_PK]] 기준으로 hotelroom FK 를 등록 하는 메서드
    //RoomController
    @Transactional
    public void registerHotelRoomForMember(MemberDTO memberDTO, HotelRoomDTO hotelRoomDTO) {
        // 1️⃣ 회원 정보 조회
        Member member = memberRepository.findById(memberDTO.getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보 없음 - 회원 번호: " + memberDTO.getMemberNum()));

        // 2️⃣ 호텔룸 저장 (Member와 연결)
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);
        hotelRoom.setMember(member); // 회원과 연결
        hotelRoom = hotelRoomRepository.save(hotelRoom);
        log.info("호텔룸 저장 완료 - 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());

        // 3️⃣ Room 엔티티 저장 (HotelRoom과 Member 관계 저장)
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .build();
        roomRepository.save(room);
        log.info("Room 엔티티 저장 완료 - 호텔룸 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());
    }


    //todo: [[HotelRoom_PK]] 기준으로 member FK 를 등록 하는 메서드v
    //todo: localhost:8090/member-insertroom
    //RoomController
    public void registerMemberForHotelRoom(HotelRoomDTO hotelRoomDTO, MemberDTO memberDTO) {
        // 1️⃣ 호텔룸 정보 조회
        HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomDTO.getHotelRoomNum())
                .orElseThrow(() -> new EntityNotFoundException("호텔룸 정보 없음 - 호텔룸 번호: " + hotelRoomDTO.getHotelRoomNum()));

        // 2️⃣ 회원 저장 (호텔룸과 연결)
        Member member = modelMapper.map(memberDTO, Member.class);
        member = memberRepository.save(member);
        log.info("회원 저장 완료 - 번호: {}", member.getMemberNum());

        // 3️⃣ 호텔룸과 회원 연결 후 저장
        hotelRoom.setMember(member);
        hotelRoom = hotelRoomRepository.save(hotelRoom);
        log.info("호텔룸과 회원 연결 완료 - 호텔룸 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());

        // 4️⃣ checkIn/checkOut 날짜 DTO에서 받아오기
        LocalDate checkInDate = LocalDate.from(memberDTO.getCheckInDate().atStartOfDay());
        LocalDate checkOutDate = LocalDate.from(memberDTO.getCheckOutDate().atStartOfDay());

        // 5️⃣ Room 엔티티 저장
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .build();
        log.info("체크인: {}, 체크아웃: {}", checkInDate, checkOutDate);
        roomRepository.save(room);
        log.info("Room 엔티티 저장 완료 - 호텔룸 번호: {}, 회원 번호: {}, 체크인: {}, 체크아웃: {}",
                hotelRoom.getHotelRoomNum(), member.getMemberNum(), checkInDate, checkOutDate);
    }



    //Room 페이지에 있는 정보를 가져 List 메서드
    public Page<RoomDTO> getRooms(Pageable pageable) {
        Page<Room> rooms = roomRepository.findAll(pageable);

        return rooms.map(room -> {
            // Null 체크 후 변환
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);


            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomName(room.getHotelRoom().getHotelRoomName());
                roomDTO.setHotelRoomPhone(room.getHotelRoom().getHotelRoomPhone());
            }

            if (room.getMember() != null) {
                roomDTO.setMemberName(room.getMember().getMemberName());
                roomDTO.setMemberEmail(room.getMember().getMemberEmail());

            }
            // ✅ 수동 매핑 (중요!)
            roomDTO.setCheckInDate(room.getCheckInDate());
            roomDTO.setCheckOutDate(room.getCheckOutDate());


            return roomDTO;
        });
    }


    //hotelroom을 참조하고 있는 member리스트 조회
    public Page<MemberDTO> getMembersByHotelRoom(Long hotelRoomNum, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByHotelRoom_HotelRoomNum(hotelRoomNum, pageable);

        return rooms.map(room -> {
            Member member = room.getMember();
            return MemberDTO.builder()
                    .memberNum(member.getMemberNum())
                    .memberName(member.getMemberName())
                    .memberPhone(member.getMemberPhone())
                    .memberEmail(member.getMemberEmail())
                    .build();
        });
    }

    //Member를 참조 하고 이쓴ㄴ hotelRoom리스트 조회
    public Page<HotelRoomDTO> getHotelRoomsByMember(Long memberNum, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByMember_MemberNum(memberNum, pageable);

        return rooms.map(room -> {
            HotelRoom hotelRoom = room.getHotelRoom();
            return HotelRoomDTO.builder()
                    .hotelRoomNum(hotelRoom.getHotelRoomNum())
                    .hotelRoomName(hotelRoom.getHotelRoomName())
                    .hotelRoomType(hotelRoom.getHotelRoomType())
                    .hotelRoomPrice(hotelRoom.getHotelRoomPrice())
                    .build();
        });
    }





}
