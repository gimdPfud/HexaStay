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

/**************************************************
 * 클래스명 : RoomCareServiceImpl
 * 기능   : 객실 관리 요청(룸 케어) 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다. (RoomCareService 인터페이스 구현)
 * 전달받은 룸 케어 요청 DTO를 엔티티로 변환하고, 연관된 호텔 객실 정보를 설정한 후,
 * 데이터베이스에 저장하는 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-24
 * 수정일 : 2025-05-09
 * 주요 메소드/기능 : RoomCareInsert (룸 케어 요청 등록)
 **************************************************/

@Service
@Log4j2
@RequiredArgsConstructor

public class RoomCareServiceImpl implements RoomCareService {

    private final RoomCareRepository roomCareRepository;
    private final ModelMapper modelMapper;
    private final HotelRoomRepository hotelRoomRepository;

    /**************************************************
     * 메소드명 : RoomCareInsert
     * 룸 케어 요청 등록
     * 기능: 전달받은 `RoomCareDTO` 데이터를 `RoomCare` 엔티티로 변환하고,
     * DTO에 포함된 호텔 객실 번호(`hotelRoomNum`)를 사용하여 해당 `HotelRoom` 엔티티를 조회합니다.
     * 조회된 `HotelRoom` 엔티티와의 연관 관계를 설정한 후, 룸 케어 요청 정보를 데이터베이스에 저장합니다.
     * @param roomCareDTO RoomCareDTO : 등록할 룸 케어 요청 정보를 담고 있는 DTO.
     * 호텔 객실 번호(`hotelRoomNum`)를 포함해야 합니다.
     * @throws IllegalArgumentException : DTO에 명시된 호텔 객실 번호에 해당하는 `HotelRoom` 엔티티가 존재하지 않는 경우,
     * 또는 `hotelRoomNum`이 DTO에 없는 경우.
     * 작성자 : 김윤겸
     * 등록일 : -
     * 수정일 : -
     **************************************************/

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
