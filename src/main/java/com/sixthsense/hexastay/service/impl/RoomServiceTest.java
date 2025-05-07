package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomServiceTest {

    private final RoomRepository roomRepository;



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



        // 4. 연결된 회원 정보로 권한 설정
        Member member = matchedRoom.getMember();
        String role = (member.getMemberRole() == null || member.getMemberRole().isBlank()) ? "USER" : member.getMemberRole();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // 5. CustomMemberDetails 생성
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member, authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customMemberDetails,
                member.getMemberPassword(),
                authorities
        );

        // 6. SecurityContext 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 7. 세션에도 저장
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        log.info("✅ 로그인 완료 - 이메일: {}, 룸 번호: {}", member.getMemberEmail(), matchedRoom.getRoomNum());

        // 8. 최종적으로 찾은 룸 반환
        return matchedRoom;
    }

    //todo : hotelRoomStatus 가 "checkin" 상태에서만 roompassword 가 일치 하면 접속이 가능
    public Room readRoomByCheckinPassword(String roomPassword) {

        // 1. 호텔룸 번호로 연결된 룸 리스트 조회
        Room matchedRoom = roomRepository.findCheckinRoomByPassword(roomPassword)
                .orElseThrow(() -> new IllegalArgumentException("체크인 상태의 룸 중에서 비밀번호가 일치하는 룸이 없습니다."));

        // ✅ 2. 추가 검증: 호텔룸 상태가 체크인인지 확인
        String hotelRoomStatus = matchedRoom.getHotelRoom().getHotelRoomStatus();
        if (!"checkin".equalsIgnoreCase(hotelRoomStatus)) {
            throw new IllegalStateException("이미 체크아웃된 객실입니다. 접속할 수 없습니다.");
        }


        // 4. 연결된 회원 정보로 권한 설정
        Member member = matchedRoom.getMember();
        String role = (member.getMemberRole() == null || member.getMemberRole().isBlank()) ? "USER" : member.getMemberRole();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // 5. CustomMemberDetails 생성
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member, authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customMemberDetails,
                member.getMemberPassword(),
                authorities
        );

        // 6. SecurityContext 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 7. 세션에도 저장
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        log.info("✅ 로그인 완료 - 이메일: {}, 룸 번호: {}", member.getMemberEmail(), matchedRoom.getRoomNum());

        // 8. 최종적으로 찾은 룸 반환
        return matchedRoom;
    }




}
