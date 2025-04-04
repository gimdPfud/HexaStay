package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.dto.OrderRoomDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.OrderRoom;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.OrderRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderRoomImpl {

    //orderRepository
    private final OrderRoomRepository orderRoomRepository;

    //HotelRoomRepository
    private final HotelRoomRepository hotelRoomRepository;

    //MemberRepository
    private final MemberRepository memberRepository;

    //ModelMapper
    private final ModelMapper modelMapper = new ModelMapper();

    //Service 없이 오더 테이블 에서 메서드 구현
    public void insertOrderMemRoom(OrderRoomDTO orderRoomDTO) {

        //Order Entity DTO로 변환
        OrderRoom orderRoom =
                modelMapper.map(orderRoomDTO, OrderRoom.class);

        //HotelRoom pk 가져 오기
        HotelRoom hotelRoom =
                hotelRoomRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        //member pk 가져 오기
        Member member =
                memberRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        //반환된 DTO에 변환된 값 SET 해주기
        orderRoom.setHotelRoom(hotelRoom);
        orderRoom.setMember(member);

        //변환된 모든 값을 가져 온거를 order 테이블에 save해주기
        orderRoomRepository.save(orderRoom);

    }



}
