package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.dto.MaechulRoomDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.MaechulRoom;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.MaechulRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderRoomImpl {

    //orderRepository
    private final MaechulRoomRepository maechulRoomRepository;

    //HotelRoomRepository
    private final HotelRoomRepository hotelRoomRepository;

    //MemberRepository
    private final MemberRepository memberRepository;

    //ModelMapper
    private final ModelMapper modelMapper = new ModelMapper();

    //Service 없이 오더 테이블 에서 메서드 구현
    public void insertOrderMemRoom(MaechulRoomDTO maechulRoomDTO) {

        //Order Entity DTO로 변환
        MaechulRoom maechulRoom =
                modelMapper.map(maechulRoomDTO, MaechulRoom.class);

        //HotelRoom pk 가져 오기
        HotelRoom hotelRoom =
                hotelRoomRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        //member pk 가져 오기
        Member member =
                memberRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        //반환된 DTO에 변환된 값 SET 해주기
        maechulRoom.setHotelRoom(hotelRoom);
        maechulRoom.setMember(member);

        //변환된 모든 값을 가져 온거를 order 테이블에 save해주기
        maechulRoomRepository.save(maechulRoom);

    }



}
