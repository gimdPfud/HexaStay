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

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomMenuServiceImpl implements RoomMenuService {

    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper = new ModelMapper();

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

    @Override
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable) {
        log.info("룸서비스 상품 리스트 서비스 진입");

        Page<RoomMenu> roomMenus = roomMenuRepository.findAll(pageable);

        roomMenus.map(roomMenu -> modelMapper.map(roomMenu, RoomMenuDTO.class));
        log.info("변환된 DTO" + roomMenus);

        return roomMenus.map(roomMenu -> modelMapper.map(roomMenu, RoomMenuDTO.class));



    }

}
