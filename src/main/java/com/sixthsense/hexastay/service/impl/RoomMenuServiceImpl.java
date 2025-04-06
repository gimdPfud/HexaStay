package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomMenuServiceImpl implements RoomMenuService {

    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    /**************************************************
     * 룸서비스 메뉴 등록
     * 기능 : 룸서비스 메뉴를 등록하는 서비스
     * 설명 : 전달된 RoomMenuDTO를 엔티티로 변환하여 DB에 저장하고,
     *        다시 DTO로 변환하여 클라이언트에게 반환
     **************************************************/

    @Override
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO) {
        log.info("룸서비스 아이템 등록 서비스 진입" + roomMenuDTO);

        // 모델맵퍼로 dto 변환
        RoomMenu roomMenu =modelMapper.map(roomMenuDTO, RoomMenu.class);

        roomMenu = roomMenuRepository.save(roomMenu);

        // 다시 DTO로 변환
        roomMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

        log.info("디티오로 변환된 등록 값" + roomMenuDTO);
        return roomMenuDTO;

    }

    /**************************************************
     * 룸서비스 메뉴 리스트 조회
     * 기능 : 룸서비스 메뉴의 목록을 페이지네이션 처리하여 반환
     * 설명 : Pageable을 사용하여 페이지 단위로 메뉴 리스트를 조회하고,
     *        해당 리스트를 DTO로 변환하여 반환
     **************************************************/

    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword) {
        log.info("룸서비스 상품 리스트 서비스 진입");

        Page<RoomMenu> roomMenuPage;

        if ("S".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            roomMenuPage = roomMenuRepository.findByRoomMenuNameContaining(keyword, pageable);
        } else if ("P".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            try {
                int price = Integer.parseInt(keyword);  // 가격을 숫자로 변환
                roomMenuPage = roomMenuRepository.findByRoomMenuPriceGreaterThan(price, pageable);  // 정확한 가격 일치
            } catch (NumberFormatException e) {
                // 숫자가 아닌 값을 입력한 경우, 전체 검색
                roomMenuPage = roomMenuRepository.findAll(pageable);
            }
        } else {
            roomMenuPage = roomMenuRepository.findAll(pageable);  // 기본 전체 조회
        }

        Page<RoomMenuDTO> roomMenuDTOList = roomMenuPage.map(roomMenuList -> modelMapper.map(roomMenuList, RoomMenuDTO.class));
        return roomMenuDTOList;
    }



    /**************************************************
     * 룸서비스 메뉴 상세 보기
     * 기능 : 특정 메뉴의 상세 정보를 조회
     * 설명 : 메뉴 번호를 이용해 DB에서 해당 메뉴를 조회하고,
     *        이를 DTO로 변환하여 반환
     **************************************************/

    @Override
    public RoomMenuDTO read(Long num) {
        log.info("상세보기 페이지 서비스 진입" + num);

        Optional<RoomMenu> optionalRoomMenu =
                roomMenuRepository.findById(num);
        // inter를 long으로 형 변환

        RoomMenuDTO menuDTO = modelMapper.map(optionalRoomMenu, RoomMenuDTO.class);
        log.info("변환된 dto read service의 값" + menuDTO);

        return menuDTO;

    }

    /**************************************************
     * 룸서비스 메뉴 수정
     * 기능 : 룸서비스 메뉴 정보를 수정
     * 설명 : 전달된 RoomMenuDTO를 엔티티로 변환하고, 해당 메뉴를 수정한 후,
     *        수정된 엔티티를 다시 DTO로 변환하여 반환
     **************************************************/

    @Override
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO) {
        log.info("업데이트 서비스 진입: " + roomMenuDTO);

        try {
            RoomMenu roomMenu = modelMapper.map(roomMenuDTO, RoomMenu.class);

            roomMenu = roomMenuRepository.save(roomMenu);

            RoomMenuDTO updatedMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

            return updatedMenuDTO;

        } catch (Exception e) {
            log.error("데이터 수정에 실패함: " + e.getMessage(), e);  //
            throw new RuntimeException("데이터 수정에 실패했습니다.", e);
        }
    }

    @Override
    public Page<RoomMenu> getMenuCategori(String categori, Pageable pageable) {

            // 예시로 카테고리에 맞는 음식 목록을 필터링
            if ("koreanfood".equals(categori)) {
                return roomMenuRepository.findByRoomMenuCategory("koreanfood", pageable);
            } else if ("chinafood".equals(categori)) {
                return roomMenuRepository.findByRoomMenuCategory("chinafood", pageable);
            } else if ("appetizer".equals(categori)) {
                return roomMenuRepository.findByRoomMenuCategory("appetizer", pageable);
            } else if ("usfood".equals(categori)) {
                return roomMenuRepository.findByRoomMenuCategory("usfood", pageable);
            } else {
                return roomMenuRepository.findAll(pageable);
            }
        }

    /**************************************************
     * 룸서비스 메뉴 삭제
     * 기능 : 특정 메뉴를 삭제하는 서비스
     * 설명 : 메뉴 번호로 해당 메뉴를 찾아 삭제
     **************************************************/

    @Override
    public void delete(Long num) {

        log.info("삭제 서비스 진입" + num);
        roomMenuRepository.deleteById(num);

        log.info("삭제완료 db를 확인하세요.");

    }

}
