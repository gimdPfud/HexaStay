package com.sixthsense.hexastay.service.impl;


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
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.time.LocalDateTime;
import java.util.List;

import java.util.ArrayList;
import jakarta.servlet.http.HttpSession;

//todo: DB 명 _ Room
@Service
@RequiredArgsConstructor
@Log4j2
public class RoomServiceImpl {


    //시큐리티에 있는 함수 및 클래스
    private final AuthenticationManager authenticationManager;

    //google 메일 발송용 서비스 메소드
    private final MailService mailService;

    private final HotelRoomRepository hotelRoomRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    //메일 재발송용 서비스 로직
    @Transactional
    public void resendRoomReservationMail(Long roomNum) {
        Room room = roomRepository.findById(roomNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 Room을 찾을 수 없습니다."));

        Member member = room.getMember();
        HotelRoom hotelRoom = room.getHotelRoom();

        // 필드만 추출해서 넘김 - mailservice 에 각각의 파라미터로 받는 로직이 구성되어 있음
        mailService.sendRoomReservationEmailpa(
                member.getMemberEmail(),
                member.getMemberName(),
                hotelRoom.getHotelRoomName(),
                room.getCheckInDate(),
                room.getCheckOutDate(),
                room.getRoomPassword(),
                hotelRoom.getHotelRoomNum()
        );
    }


    //hotelroom.hotelroomStatus 상태 즉 checkin , checkout 상태에 따른 list page
    public Page<RoomDTO> findRoomsByHotelRoomStatus(Long companyNum, String status, Pageable pageable) {


        Page<Room> roomPage = roomRepository.findByHotelRoom_Company_CompanyNumAndHotelRoom_HotelRoomStatus(companyNum, status, pageable);

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
    //RoomController - 예약 등록
    @Transactional
    public void memberPkRoominsert(MemberDTO memberDTO, HotelRoomDTO hotelRoomDTO) throws MessagingException {
        // 1️⃣ 회원 정보 조회
        Member member = memberRepository.findById(memberDTO.getMemberNum())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보 없음 - 회원 번호: " + memberDTO.getMemberNum()));

        HotelRoom hotelRoom =
                hotelRoomRepository.findById(hotelRoomDTO.getHotelRoomNum()).orElseThrow(EntityNotFoundException::new);


        // 4️⃣ checkIn/checkOut 날짜 DTO에서 받아오기
        LocalDateTime checkInDateTime = LocalDateTime.from(hotelRoomDTO.getCheckInDate());
        LocalDateTime checkOutDateTime = LocalDateTime.from(hotelRoomDTO.getCheckOutDate());
        // 3️⃣ Room 엔티티 저장 (HotelRoom과 Member 관계 저장)
        Room room = Room.builder()
                .hotelRoom(hotelRoom)
                .member(member)
                .checkInDate(checkInDateTime)
                .checkOutDate(checkOutDateTime)
                /* fixme: 예약 테이블에서 표시 여부  VISIBLE(표시), HIDDEN(숨기기) 초기 값은 String "VISIBLE" 설정*/
                .roomDisplayStatus("VISIBLE")
                .roomPassword(memberDTO.getRoomPassword())
                .build();
        roomRepository.save(room);

        // 5️⃣ 이메일 발송
        //room 테이블에서 저장후에 room 안에 memberFK 기준으로 찾아와서 멤버의 이메일로 이메이 발송
        try {
            // ✅ room 기준으로 mailService 호출
            mailService.sendRoomReservationEmail(room);
            log.info("메일 발송 성공 - 수신자: {}", member.getMemberEmail());
        } catch (Exception e) {
            log.error("메일 발송 실패 - 수신자: {}, 오류: {}", member.getMemberEmail(), e.getMessage());
        }

        log.info("Room 엔티티 저장 완료 - 호텔룸 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());
    }


    //todo: [[HotelRoom_PK]] 기준으로 member FK 를 등록 하는 메서드v
    //todo: localhost:8090/member-insertroom
    //RoomController
    public void hotelRoomPkMemberinsert(HotelRoomDTO hotelRoomDTO, MemberDTO memberDTO) throws MessagingException {
        // 1️⃣ 호텔룸 정보 조회
        HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomDTO.getHotelRoomNum())
                .orElseThrow(() -> new EntityNotFoundException("호텔룸 정보 없음 - 호텔룸 번호: " + hotelRoomDTO.getHotelRoomNum()));

        // 2️⃣ 비회원 저장 (호텔룸과 연결)
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

                /* todo: 예약 테이블에서 표시 여부  VISIBLE(표시), HIDDEN(숨기기) 초기 값은 String "VISIBLE" 설정*/
                .roomDisplayStatus("VISIBLE")

                .roomPassword(memberDTO.getRoomPassword()) // ✅ 추가된 부분
                .build();
        log.info("체크인: {}, 체크아웃: {}", checkInDate, checkOutDate);
        roomRepository.save(room);

        //room 테이블에서 저장후 메일 방송 로직 실행
        //MailService
        // 5️⃣ 이메일 발송
        try {
            // ✅ room 기준으로 mailService 호출
            mailService.sendRoomReservationEmail(room);
            log.info("메일 발송 성공 - 수신자: {}", member.getMemberEmail());
        } catch (Exception e) {
            log.error("메일 발송 실패 - 수신자: {}, 오류: {}", member.getMemberEmail(), e.getMessage());
        }

        log.info("Room 엔티티 저장 완료 - 호텔룸 번호: {}, 회원 번호: {}", hotelRoom.getHotelRoomNum(), member.getMemberNum());
    }

    //패스워드 중복 체크
    public boolean isRoomPasswordAvailable(String roomPassword) {
        long count = roomRepository.countByRoomPassword(roomPassword);
        return count == 0;
    }


    //Room 페이지에 있는 정보를 가져 List 메서드
    public Page<RoomDTO> getRooms(Long companyNum, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByHotelRoom_Company_CompanyNum(companyNum, pageable);

        return rooms.map(room -> {
            // Null 체크 후 변환
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);


            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomName(room.getHotelRoom().getHotelRoomName());
                roomDTO.setHotelRoomPhone(room.getHotelRoom().getHotelRoomPhone());
                roomDTO.setHotelRoomStatus(room.getHotelRoom().getHotelRoomStatus());
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

    /*중간테이블 쌓이는거 방지 하는 버튼 만드는 로직*/
    public Page<RoomDTO> findRoomsByDisplayStatus(String visionstatus, Pageable pageable) {
        return roomRepository.findByRoomDisplayStatus(visionstatus, pageable)
                .map(room -> modelMapper.map(room, RoomDTO.class));
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
// 호텔룸 번호를 받아서 룸을 찾고, 로그인 인증까지 해주는 메소드
    public Room readRoomByHotelRoomNum(Long hotelRoomNum, String roomPassword) {

        // 1. 호텔룸 번호로 연결된 룸 리스트 조회
        List<Room> rooms = roomRepository.findByHotelRoomNum(hotelRoomNum);

        // 2. 연결된 룸이 없으면 예외
        if (rooms.isEmpty()) {
            throw new EntityNotFoundException("해당 호텔룸에 연결된 룸 정보가 없습니다.");
        }

        // 3. 비밀번호가 일치하는 Room 찾기
        Room matchedRoom = rooms.stream()
                .filter(r -> r.getRoomPassword() != null && r.getRoomPassword().equals(roomPassword))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 일치하는 룸이 없습니다."));

        // 4. 선택한 룸에 연결된 회원(Member) 정보를 가져온다
        Member member = matchedRoom.getMember();

        // 5. 회원의 역할(Role)을 가져온다 (예: USER, ADMIN)
        String role = member.getMemberRole();

        // 6. 역할이 없으면 기본으로 USER 역할을 넣어준다
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }

        // 7. 권한 리스트를 만들어서 ROLE_ 앞에 붙여준다
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        // 8. 회원 정보를 담은 커스텀 객체를 만든다 (UserDetails 역할)
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member, authorities);

        log.info("로그인 처리 - 회원: {}, 권한: {}", member.getMemberEmail(), authorities);

        // 9. 인증(Authentication) 객체를 만든다
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customMemberDetails,        // 로그인 사용자 정보 (UserDetails)
                member.getMemberPassword(), // 비밀번호 (실제로는 필요없지만 넣음)
                authorities                  // 권한 리스트
        );

        // 10. 비어있는 SecurityContext를 새로 만든다
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 11. 만든 인증 객체를 Context에 넣어준다
        context.setAuthentication(authentication);

        // 12. Context를 SecurityContextHolder에 저장한다 (로그인 완료!)
        SecurityContextHolder.setContext(context);

        // 13. 세션(Session)에도 SecurityContext를 저장해준다 (브라우저 닫기 전까지 기억하게)
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        log.info("인증 정보 설정 완료: {}", authentication);
        log.info("✅ 로그인 완료 - 이메일: {}, 룸 번호: {}", member.getMemberEmail(), matchedRoom.getRoomNum());

        // 14. 최종적으로 찾은 룸을 반환한다
        return matchedRoom;
    }

    //roomlist 검색용 서비스 조건 : member 의 이름과 이메일 기준
    //todo: room/roomlist.html - 검색 의 적용
    public Page<RoomDTO> searchRoomsByMemberKeywordPaged(String keyword, Pageable pageable) {
        Page<Room> rooms = roomRepository.searchRoomsByMemberNameOrEmailPaged(keyword, pageable);
        return rooms.map(room -> modelMapper.map(room, RoomDTO.class));
    }

    //검색용 추가1
    public Page<RoomDTO> searchRoomsByStatusAndKeyword(Long companyNum, String status, String keyword, Pageable pageable) {

        return roomRepository.findByCompanyAndStatusAndKeyword(companyNum, status, keyword, pageable)
                .map(room -> modelMapper.map(room, RoomDTO.class));
    }

    //검색용 추가2
    public Page<RoomDTO> searchRoomsByDisplayStatusAndKeyword(Long companyNum, String displayStatus, String keyword, Pageable pageable) {
        return roomRepository.findByDisplayStatusAndKeyword(companyNum, displayStatus, keyword, pageable)
                .map(room -> modelMapper.map(room, RoomDTO.class));
    }

    //검색용 추가3
    public Page<RoomDTO> searchRoomsByCompanyAndKeyword(Long companyNum, String keyword, Pageable pageable) {
        return roomRepository.findByCompanyAndKeyword(companyNum, keyword, pageable)
                .map(room -> modelMapper.map(room, RoomDTO.class));
    }


    public Room authenticateRoomByHotelRoomNumAndPassword(Long hotelRoomNum, String inputPassword) {
        // 1. 해당 호텔룸에 연결된 '체크인' 상태의 Room 리스트 가져오기
        List<Room> rooms = roomRepository.findCheckinRoomsByHotelRoomNum(hotelRoomNum);

        if (rooms.isEmpty()) {
            throw new EntityNotFoundException("체크인 상태의 룸이 없습니다.");
        }

        // 2. 입력한 패스워드가 일치하는 Room 찾기
        Room matchedRoom = rooms.stream()
                .filter(room -> room.getRoomPassword().equals(inputPassword))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 일치하지 않습니다."));

        // 3. 로그인 처리
        Member member = matchedRoom.getMember();
        String role = member.getMemberRole() == null ? "USER" : member.getMemberRole();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member, authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customMemberDetails, member.getMemberPassword(), authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        log.info("인증 성공: {} (roomNum: {})", member.getMemberEmail(), matchedRoom.getRoomNum());
        return matchedRoom;
    }


    public void updateRoomDisplayStatus(Long roomNum, String status) {
        Room room = roomRepository.findById(roomNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 룸이 존재하지 않습니다."));

        if (room.getRoomDisplayStatus() == null) {
            room.setRoomDisplayStatus("VISIBLE");
        }
        room.setRoomDisplayStatus(status);

        roomRepository.save(room);
    }


}
