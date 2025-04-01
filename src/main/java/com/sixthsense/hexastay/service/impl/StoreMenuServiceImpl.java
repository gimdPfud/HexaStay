/***********************************************
 * 클래스명 : StoreMenuServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StoreMenuDTO;
import com.sixthsense.hexastay.entity.StoreMenu;
import com.sixthsense.hexastay.repository.StoreMenuRepository;
import com.sixthsense.hexastay.service.StoreMenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class StoreMenuServiceImpl implements StoreMenuService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoreMenuRepository storeMenuRepository;

    /*
     * 메소드명 : insert
     * 인수 값 : StoreMenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 데이터베이스에 추가하고, 등록한 객체의 pk를 반환함.
     * */
    @Override
    public Long insert(StoreMenuDTO storeMenuDTO) {
        StoreMenu storeMenu = modelMapper.map(storeMenuDTO, StoreMenu.class);
        storeMenu = storeMenuRepository.save(storeMenu);
        return storeMenu.getStoreMenuNum();
    }

    /*
     * 메소드명 : read
     * 인수 값 : Long
     * 리턴 값 : StoreMenuDTO
     * 기  능 : pk를 받아 해당하는 데이터를 찾아 반환함.
     * */
    @Override
    public StoreMenuDTO read(Long pk) {
        StoreMenu storeMenu = storeMenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        StoreMenuDTO data = modelMapper.map(storeMenu,StoreMenuDTO.class);
        return data;
    }

    /*
     * 메소드명 : modify
     * 인수 값 : StoreMenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 수정한 후, 수정한 객체의 pk를 반환한다.
     * */
    @Override
    public Long modify(StoreMenuDTO storeMenuDTO) {
        StoreMenu entity = storeMenuRepository.findById(storeMenuDTO.getStoreMenuNum()).orElseThrow(EntityNotFoundException::new);
        entity.setStoreMenuContent(storeMenuDTO.getStoreMenuContent());
        entity.setStoreMenuPrice(storeMenuDTO.getStoreMenuPrice());
        entity.setStoreMenuCategory(storeMenuDTO.getStoreMenuCategory());
        entity.setStoreMenuName(storeMenuDTO.getStoreMenuName());
        entity.setStoreMenuStatus(storeMenuDTO.getStoreMenuStatus());
        return entity.getStoreMenuNum();
    }

    /*
     * 메소드명 : list
     * 인수 값 : Pageable
     * 리턴 값 : Page<StoreMenuDTO>
     * 기  능 : 데이터베이스에 존재하는 모든 StoreMenu를 페이지타입으로 가져온다.
     * */
    @Override
    public Page<StoreMenuDTO> list(Pageable pageable) {
        Page<StoreMenu> storeMenuPage = storeMenuRepository.findAll(pageable);
        Page<StoreMenuDTO> storeMenuDTOPage = storeMenuPage.map(data -> modelMapper.map(data,StoreMenuDTO.class));
        return storeMenuDTOPage;
    }

    /*
     * 메소드명 : list
     * 인수 값 : String status, Pageable
     * 리턴 값 : Page<StoreMenuDTO>
     * 기  능 : 입력받은 활성화 상태와 동일한 status를 가진 데이터만 페이지 타입으로 가져온다.
     * */
    @Override
    public Page<StoreMenuDTO> list(String status, Pageable pageable) {
        Page<StoreMenu> storeMenuPage = storeMenuRepository.findByStoreMenuStatus(status, pageable);
        Page<StoreMenuDTO> storeMenuDTOPage = storeMenuPage.map(data -> modelMapper.map(data,StoreMenuDTO.class));
        return storeMenuDTOPage;
    }
}
