/***********************************************
 * 클래스명 : StoremenuServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.entity.Storemenu;
import com.sixthsense.hexastay.entity.StoremenuOption;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Service
@Log4j2
@Transactional
public class StoremenuServiceImpl implements StoremenuService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoremenuRepository storemenuRepository;

    /*
     * 메소드명 : insert
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 데이터베이스에 추가하고, 등록한 객체의 pk를 반환함.
     * */
    @Override
    public Long insert(StoremenuDTO storemenuDTO) throws IOException {
        storemenuDTO.setStoremenuStatus("alive");
        Storemenu storemenu = modelMapper.map(storemenuDTO, Storemenu.class);
        /*옵션 리스트 변환*/
        if(storemenuDTO.getStoremenuOptionDTOList()!=null&&!storemenuDTO.getStoremenuOptionDTOList().isEmpty()){
            Storemenu finalStoremenu = storemenu;
            storemenu.setStoremenuOptionList(
                    storemenuDTO.getStoremenuOptionDTOList().stream()
                            .map(data-> {
                                StoremenuOption optionEntity = modelMapper.map(data, StoremenuOption.class);
                                optionEntity.setStoremenu(finalStoremenu);
                                return optionEntity;
                            })
                            .toList()
            );
        }
        storemenu = storemenuRepository.save(storemenu);

        if(storemenuDTO.getStoremenuImg()!=null&& !storemenuDTO.getStoremenuImg().isEmpty()){
            String fileOriginalName = storemenuDTO.getStoremenuImg().getOriginalFilename();
            String fileFirstName = storemenu.getStoremenuNum() + "_" + storemenuDTO.getStoreNum();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            storemenuDTO.setStoremenuImgMeta("/store/menu/"+fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"),"store/menu/"+fileName);
            Path createPath = Paths.get(System.getProperty("user.dir" ),"store/menu/");
            if(!Files.exists(createPath)){
                Files.createDirectory(createPath);
            }
            storemenuDTO.getStoremenuImg().transferTo(uploadPath.toFile());
        }
        storemenu.setStoremenuImgMeta(storemenuDTO.getStoremenuImgMeta());
        storemenuRepository.save(storemenu);
        return storemenu.getStoremenuNum();
    }

    /*
     * 메소드명 : read
     * 인수 값 : Long
     * 리턴 값 : StoremenuDTO
     * 기  능 : pk를 받아 해당하는 데이터를 찾아 반환함.
     * */
    @Override
    public StoremenuDTO read(Long pk) {
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        StoremenuDTO data = modelMapper.map(storemenu, StoremenuDTO.class);
        return data;
    }

    /*
     * 메소드명 : modify
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 수정한 후, 수정한 객체의 pk를 반환한다.
     * */
    @Override
    public Long modify(StoremenuDTO storemenuDTO) {
        Storemenu entity = storemenuRepository.findById(storemenuDTO.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        entity.setStoremenuContent(storemenuDTO.getStoremenuContent());
        entity.setStoremenuPrice(storemenuDTO.getStoremenuPrice());
        entity.setStoremenuCategory(storemenuDTO.getStoremenuCategory());
        entity.setStoremenuName(storemenuDTO.getStoremenuName());
        entity.setStoremenuStatus("alive");
        return entity.getStoremenuNum();
    }


    /*
     * 메소드명 : list
     * 인수 값 : Long storeNum, String status, Pageable pageable
     * 리턴 값 : Page<StoremenuDTO>
     * 기  능 : 활성화상태가 입력받은값과 동일하면서, fk가 해당되는 데이터만 page로 가져온다.
     * */
    @Override
    public List<StoremenuDTO> list(Long storeNum, String status) {
        List<Storemenu> storemenuList= storemenuRepository.findByStoreStoreNumAndStoremenuStatus(storeNum,status);
        List<StoremenuDTO> list = storemenuList.stream().map(data -> modelMapper.map(data, StoremenuDTO.class)).toList();
        return list;
    }

    @Override
    public List<StoremenuDTO> list(Long storeNum) {
        List<Storemenu> storemenuList = storemenuRepository.findAll(storeNum);
        List<StoremenuDTO> list = storemenuList.stream().map(data->modelMapper.map(data,StoremenuDTO.class)).toList();
        return list;
    }

    @Override
    public List<StoremenuDTO> list(Long storeNum, String category, String status) {
        List<Storemenu> storemenuList= storemenuRepository.findCateg(storeNum, category, status);
        List<StoremenuDTO> list = storemenuList.stream().map(data -> modelMapper.map(data, StoremenuDTO.class)).toList();
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
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuStatus("deleted");
        return storemenu.getStore().getStoreNum();
    }


    /*
     * 메소드명 : restore
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 alive로 바꾼다.
     * */
    @Override
    public Long restore(Long pk) {
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuStatus("alive");
        return storemenu.getStore().getStoreNum();
    }
}
