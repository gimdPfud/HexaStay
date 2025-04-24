package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomCareDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.RoomCare;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.RoomCareRepository;
import com.sixthsense.hexastay.service.RoomCareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@Log4j2
@RequiredArgsConstructor

public class RoomCareServiceImpl implements RoomCareService {

    private final RoomCareRepository roomCareRepository;
    private final ModelMapper modelMapper;
    private final HotelRoomRepository hotelRoomRepository;


    @Override
    public void RoomCareInsert(RoomCareDTO roomCareDTO) {
        log.info("룸케어 서비스 등록 진입");

        RoomCare roomCare = modelMapper.map(roomCareDTO, RoomCare.class);

        // 2. hotelRoomNum이 DTO에 있다면, 그걸로 DB에서 hotelRoom 조회
        Long hotelRoomNum = roomCareDTO.getHotelRoomNum(); // 이 메서드는 DTO에 있어야 함

        HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 호텔룸이 존재하지 않습니다: id=" + hotelRoomNum));

        // 3. 연관관계 설정
        roomCare.setHotelRoom(hotelRoom);

        roomCareRepository.save(roomCare);

        log.info("룸케어 등록 완료: {}", roomCare);

    }

}
