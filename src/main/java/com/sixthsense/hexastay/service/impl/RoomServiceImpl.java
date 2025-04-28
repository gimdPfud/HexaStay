package com.sixthsense.hexastay.service.impl;

import com.beust.ah.A;
import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomServiceImpl {
    //todo: DB 명 _ Room

    //시큐리티에 있는 함수 및 클래스
    private final AuthenticationManager authenticationManager;

    private final HotelRoomRepository hotelRoomRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    //hotelroom.hotelroomStatus 상태 즉 checkin , checkout 상태에 따른 list page
    public Page<RoomDTO> findRoomsByHotelRoomStatus(String status, Pageable pageable) {
        Page<Room> roomPage = roomRepository.findByHotelRoomStatus(status, pageable);

        return roomPage.map(room -> {
            // 1. 기본 Room → RoomDTO 매핑
            RoomDTO dto = modelMapper.map(room, RoomDTO.class);

            // 2. hotelRoom 정보 매핑
            if (room.getHotelRoom() != null) {
                HotelRoomDTO hotelRoomDTO = modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class);
                dto.setHotelRoomDTO(hotelRoomDTO);

                dto.setHotelRoomName(room.getHotelRoom().getHotelRoomName());
                dto.setHotelRoomPhone(room.getHotelRoom().getHotelRoomPhone());
                dto.setHotelRoomStatus(room.getHotelRoom().getHotelRoomStatus());
                dto.setHotelRoomNum(room.getHotelRoom().getHotelRoomNum());
            }

            // 3. member 정보 매핑
            if (room.getMember() != null) {
                MemberDTO memberDTO = modelMapper.map(room.getMember(), MemberDTO.class);
                dto.setMemberDTO(memberDTO);

                dto.setMemberName(room.getMember().getMemberName());
                dto.setMemberEmail(room.getMember().getMemberEmail());
                dto.setMemberNum(room.getMember().getMemberNum());
            }

            return dto;
        });
    }


    //todo: [[Member_PK]] 기준으로 hotelroom FK 를 등록 하는 메서드
    //todo:http://localhost:8090/register-hotelroom
    //RoomController
    @Transactional
    public void memberPkRoominsert(MemberDTO memberDTO, HotelRoomDTO hotelRoomDTO) {
        // 1️⃣ 회원 정보 조회
        Member member = memberRepository.findById(memberDTO.getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보 없음 - 회원 번호: " + memberDTO.getMemberNum()));

        HotelRoom hotelRoom =
                hotelRoomRepository.findById(hotelRoomDTO.getHotelRoomNum()).orElseThrow(EntityNotFoundException::new);
        // 2️⃣ 호텔룸 저장 (Member와 연결)
//        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);
//        hotelRoom.setMember(member); // 회원과 연결
//        hotelRoom = hotelRoomRepository.save(hotelRoom);
//        log.info("호텔룸 저장 완료 - 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());

        // 4️⃣ checkIn/checkOut 날짜 DTO에서 받아오기
        LocalDateTime checkInDate = LocalDateTime.from(hotelRoomDTO.getCheckInDate());
        LocalDateTime checkOutDate = LocalDateTime.from(hotelRoomDTO.getCheckOutDate());
        // 3️⃣ Room 엔티티 저장 (HotelRoom과 Member 관계 저장)
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .roomPassword(memberDTO.getRoomPassword())
                .build();
        roomRepository.save(room);
        log.info("Room 엔티티 저장 완료 - 호텔룸 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());
    }


    //todo: [[HotelRoom_PK]] 기준으로 member FK 를 등록 하는 메서드v
    //todo: localhost:8090/member-insertroom
    //RoomController
    public void hotelRoomPkMemberinsert(HotelRoomDTO hotelRoomDTO, MemberDTO memberDTO) {
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
        LocalDateTime checkInDate = LocalDateTime.from(memberDTO.getCheckInDate());
        LocalDateTime checkOutDate = LocalDateTime.from(memberDTO.getCheckOutDate());

        // 5️⃣ Room 엔티티 저장
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .roomPassword(memberDTO.getRoomPassword()) // ✅ 추가된 부분
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
                roomDTO.setHotelRoomStatus(room.getHotelRoom().getHotelRoomStatus());            }

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
                    .hotelRoomProfileMeta(hotelRoom.getHotelRoomProfileMeta())
                    .build();
        });
    }

    //todo:main 페이지 가기 전에 중간 페이지 - 필수 페이지 입니다.
    //roompassword 을 찾아와서 패스워드를 인증 하는 메소드
    public boolean RoomPassword(String roomPassword) {
        return roomRepository.findRoomByRoomPassword(roomPassword).isPresent();
    }

    //Room pk 을 찾아와서 Member FK 만 수정 하는 로직
    //todo:memberByhotelRoom.html 에서 쓰이는 메소드
    @Transactional
    public void updateRoomMember(Long roomNum, Long memberNum) {
        // 존재 여부 확인 (예외처리용)
        roomRepository.findById(roomNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 룸 번호가 존재하지 않습니다."));
        memberRepository.findById(memberNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 번호가 존재하지 않습니다."));

        roomRepository.updateRoomMember(roomNum, memberNum);

        log.info("(수정/@Query) Room {} 의 Member가 {} 로 교체되었습니다.", roomNum, memberNum);
    }

    //Room Pk을 찾아와서 HotelRoom FK만 수정 하는 로직
    //todo:hotelRoomsByMember.html
    @Transactional
    public void updateHotelRoomInRoom(Long roomNum, Long hotelRoomNum) {
        Room room = roomRepository.findById(roomNum)
                .orElseThrow(() -> new IllegalArgumentException("Room을 찾을 수 없습니다."));
        HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomNum)
                .orElseThrow(() -> new IllegalArgumentException("HotelRoom을 찾을 수 없습니다."));

        room.setHotelRoom(hotelRoom);
        // @Transactional 이므로 save 없이도 flush됨 (옵션이지만 명시하면 명확)
        roomRepository.save(room);
    }

    //todo: http://localhost:8090/qr/${hotelRoomNum}
    //Room DB 에서 HotelRoomNum fk 을 찾아 와서 그 기준으로 member fk을 찾아 오는 로직
    public Room readRoomByHotelRoomNum(Long hotelRoomNum) {
        List<Room> rooms = roomRepository.findByHotelRoomNum(hotelRoomNum);

        if (rooms.isEmpty()) {
            throw new EntityNotFoundException("해당 호텔룸에 연결된 룸 정보가 없습니다.");
        }

        // 가장 첫 번째 Room으로 로그인 인증 처리
        Room room = rooms.getFirst();

        Member member = room.getMember();

        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);



        log.info("중간 맴버" + member.toString());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customMemberDetails,
//                member.getMemberEmail(), // 아이디
                null,                    // 패스워드는 의미 없음
                customMemberDetails.getAuthorities()// 권한 비어있
                                // 권한도 비워둠 (필요하면 ROLE_USER 이런거 넣어도 됨)
        );
        log.info(authentication + "dkdkdkldjlfjlkdjflkdjlfkjdlkfj");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return room;  // 🔥 전체 Room 리스트 반환
    }





}
