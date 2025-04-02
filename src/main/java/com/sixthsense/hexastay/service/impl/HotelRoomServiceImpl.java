package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.HotelRoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class HotelRoomServiceImpl implements HotelRoomService {

    //호텔룸 레퍼지토리
    private final HotelRoomRepository hotelRoomRepository;

    //변환 처리를 위한 ModelMapper
    private final ModelMapper modelMapper = new ModelMapper();

    //todo: 메소드 예외 처리는 추후에 할 예정 입니다.


    //1.등록
    @Override
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO) {

        //변환 - Memem만 DTO 타입으로 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        //처리
        hotelRoomRepository.save(hotelRoom);

    }

    //2.리스트
    @Override
    public Page<HotelRoomDTO> hotelroomList(Pageable page) {

        //********페이지 처리 ************//
        //시작 페이지 설정
        int firstPage = page.getPageNumber() - 1;

        //총 토탈 페이지 설정 - 토탈 페이지는 갯수는 여기서 설정 가능
        int pageLimites = 5;

        //페이지 재정의후 페이지 조립
        Pageable pageable =
                PageRequest.of(firstPage, pageLimites,
                        Sort.by(Sort.Direction.DESC,"hotelRoomNum"));

        //*** 변환 및 처리 작업 **//
        //엔티티 변수 선언
        Page<HotelRoom> hotelroomEntity;

        hotelroomEntity =
                hotelRoomRepository.findAll(pageable);

        //todo : memberRepository에서 검색설정후 검색 메서드 구현 예정

        //변환    - 람다식으로 변환
        Page<HotelRoomDTO> hotelRoomDTOS =
                hotelroomEntity.map(data -> modelMapper.map(data, HotelRoomDTO.class));

        //호텔룸 최종 반환 타입
        return hotelRoomDTOS;
    }

    //3.읽기
    @Override
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum) {

        //Entity 가져오기
        Optional<HotelRoom> optionalHotelRoom =
                hotelRoomRepository.findById(hotelRoomNum);

        //변환 Entity >> DTO
        HotelRoomDTO hotelRoomDTO = modelMapper.map(optionalHotelRoom, HotelRoomDTO.class);


        return hotelRoomDTO;
    }

    //4.수정
    @Override
    public void hotelroomrModify(HotelRoomDTO hotelRoomDTO) {

        //변환 - Memem만 DTO 타입으로 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        //처리
        hotelRoomRepository.save(hotelRoom);

    }

    //5.삭제
    @Override
    public void hotelroomDelet(Long hotelRoomNum) {

        //삭제처리 - HotelRoom 있는 pk를 가져와서 행 삭제 처리
        hotelRoomRepository.deleteById(hotelRoomNum);

    }
}
