/***********************************************
 * 클래스명 : StoremenuServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.dto.StoremenuOptionDTO;
import com.sixthsense.hexastay.entity.Storemenu;
import com.sixthsense.hexastay.entity.StoremenuOption;
import com.sixthsense.hexastay.repository.StoremenuOptionRepository;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.StoremenuOptionService;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
@Transactional
public class StoremenuOptionServiceImpl implements StoremenuOptionService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoremenuRepository storemenuRepository;
    private final StoremenuOptionRepository storemenuOptionRepository;

    /*
     * 메소드명 : insert
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 데이터베이스에 추가하고, 등록한 객체의 pk를 반환함.
     * */
    @Override
    public Long insert(StoremenuOptionDTO StoremenuOptionDTO) throws IOException {
        StoremenuOptionDTO.setStoremenuOptionStatus("alive");
        StoremenuOption storemenuOption = modelMapper.map(StoremenuOptionDTO, StoremenuOption.class);
        storemenuOption = storemenuOptionRepository.save(storemenuOption);
        return storemenuOption.getStoremenuOptionNum();
    }

    /*
     * 메소드명 : read
     * 인수 값 : Long
     * 리턴 값 : StoremenuDTO
     * 기  능 : pk를 받아 해당하는 데이터를 찾아 반환함.
     * */
    @Override
    public StoremenuOptionDTO read(Long pk) {
        StoremenuOption storemenuOption = storemenuOptionRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        StoremenuOptionDTO data = modelMapper.map(storemenuOption, StoremenuOptionDTO.class);
        return data;
    }

    /*
     * 메소드명 : modify
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 수정한 후, 수정한 객체의 pk를 반환한다.
     * */
    @Override
    public Long modify(StoremenuOptionDTO storemenuOptionDTO) throws IOException {
        StoremenuOption entity = storemenuOptionRepository.findById(storemenuOptionDTO.getStoremenuOptionNum()).orElseThrow(EntityNotFoundException::new);
        entity.setStoremenuOptionPrice(storemenuOptionDTO.getStoremenuOptionPrice());
        entity.setStoremenuOptionName(storemenuOptionDTO.getStoremenuOptionName());
        entity.setStoremenuOptionStatus("alive");
        return entity.getStoremenu().getStoremenuNum();
    }


    /*
     * 메소드명 : list
     * 인수 값 : Long storeNum, String status, Pageable pageable
     * 리턴 값 : Page<StoremenuDTO>
     * 기  능 : 활성화상태가 입력받은값과 동일하면서, fk가 해당되는 데이터만 page로 가져온다.
     * */
    @Override
    public List<StoremenuOptionDTO> list(Long storemenuNum, String status) {
        List<StoremenuOption> storemenuList= storemenuOptionRepository.findByStatusAndMenuNum(status,storemenuNum);
        List<StoremenuOptionDTO> list = storemenuList.stream().map(data -> modelMapper.map(data, StoremenuOptionDTO.class)).toList();
        return list;
    }

    @Override
    public List<StoremenuOptionDTO> list(Long storemenuNum) {
        List<StoremenuOption> storemenuList = storemenuOptionRepository.findAll(storemenuNum);
        List<StoremenuOptionDTO> list = storemenuList.stream().map(data->modelMapper.map(data,StoremenuOptionDTO.class)).toList();
        return list;
    }


    /*
     * 메소드명 : delete
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 deleted로 바꾼다.
     * */
    @Override
    public Long delete(Long pk) {
        StoremenuOption storemenu = storemenuOptionRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuOptionStatus("deleted");
        return storemenu.getStoremenu().getStoremenuNum();
    }


    /*
     * 메소드명 : restore
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 alive로 바꾼다.
     * */
    @Override
    public Long restore(Long pk) {
        StoremenuOption storemenu = storemenuOptionRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuOptionStatus("alive");
        return storemenu.getStoremenu().getStoremenuNum();
    }
}
