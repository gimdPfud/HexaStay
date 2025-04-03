/***********************************************
 * 클래스명 : StoreServiceImpl
 * 기능 : 외부업체 서비스 구현객체
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Store;
import com.sixthsense.hexastay.repository.StoreRepository;
import com.sixthsense.hexastay.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    /*
     * 메소드명 : insert
     * 인수 값 : StoreDTO
     * 리턴 값 : Long
     * 기  능 : Store 등록 후 등록한 객체의 pk 반환
     * */
    @Override
    public void insert(StoreDTO storeDTO) {
        Store store = modelMapper.map(storeDTO, Store.class);
        storeRepository.save(store);
    }


    /*
     * 메소드명 : read
     * 인수 값 : Long pk
     * 리턴 값 : StoreDTO
     * 기  능 : pk값으로 StoreDTO 단일 객체를 찾아옴
     * */
    @Override
    public StoreDTO read(Long pk) {
        Store store = storeRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        StoreDTO storeDTO = modelMapper.map(store, StoreDTO.class);
        return storeDTO;
    }


    /*
     * 메소드명 : modify
     * 인수 값 : StoreDTO
     * 리턴 값 : Long
     * 기  능 : storeDTO를 받아서 수정 한 후 수정한 객체의 pk값을 반환한다.
     * */
    @Override
    public Long modify(StoreDTO storeDTO) {
        Store store = storeRepository.findById(storeDTO.getStoreNum()).orElseThrow(EntityNotFoundException::new);
        store.setStoreName(storeDTO.getStoreName());
        store.setStoreEmail(storeDTO.getStoreEmail());
        store.setStorePhone(storeDTO.getStorePhone());
        store.setStoreStatus(storeDTO.getStoreStatus());
        store.setStoreCeoName(storeDTO.getStoreCeoName());
        store.setStoreAddress(storeDTO.getStoreAddress());
        store.setStorePassword(storeDTO.getStorePassword());
        return store.getStoreNum();
    }

    @Override
    public List<StoreDTO> getAllList() {
        List<Store> storeList = storeRepository.findAll("active");
        List<StoreDTO> list = storeList.stream().map(data -> modelMapper.map(data, StoreDTO.class)).toList();
        return list;
    }


    /*
     * 메소드명 : list
     * 인수 값 : String status, Pageable
     * 리턴 값 : Page<StoreDTO>
     * 기  능 : 활성화 여부를 받아서 해당되는 상점들만 페이지 타입으로 반환합니다.
     * */
    @Override
    public Page<StoreDTO> list(String status, Pageable pageable) {
        Page<Store> storePage = storeRepository.findByStoreStatus(status, pageable);
        Page<StoreDTO> storeDTOPage = storePage.map(data -> modelMapper.map(data,StoreDTO.class));
        return storeDTOPage;
    }


    /*
     * 메소드명 : list
     * 인수 값 : Pageable
     * 리턴 값 : Page<StoreDTO>
     * 기  능 : 페이징 정보를 받아 StoreDTO들을 페이지 타입으로 반환
     * */
    @Override
    public Page<StoreDTO> list(Pageable pageable) {
        Page<Store> storePage = storeRepository.findAll(pageable);
        Page<StoreDTO> storeDTOPage = storePage.map(data -> modelMapper.map(data,StoreDTO.class));
        return storeDTOPage;
    }


    /*
     * 메소드명 : delete
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Store 객체의 활성화 컬럼 데이터를 inactive 로 바꾼다.
     * */
    @Override
    public void delete(Long pk) {
        Store store = storeRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        store.setStoreStatus("inactive");
    }
}
