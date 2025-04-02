package com.sixthsense.hexastay.service.impl;

import ch.qos.logback.core.model.Model;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.service.CenterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public void centerInsert(CenterDTO centerDTO) {
        log.info("center Insert Service 진입");

        //center 등록
        Center center = modelMapper.map(centerDTO, Center.class);
        center = centerRepository.save(center);

        log.info("centerDTO를 Entity로 변환 완료 : " + center);
    }

    @Override
    public Page<CenterDTO> centerList(Pageable pageable) {
        log.info("center List Service 진입");

        //center 목록
        Page<Center> centerPage = centerRepository.findAll(pageable);
        Page<CenterDTO> centerDTOS = centerPage.map(center -> modelMapper.map(center, CenterDTO.class));

        return centerDTOS;
    }

    @Override
    public CenterDTO centerRead(Long centerNum) {
        log.info("center Read Service 진입");

        //center 상세보기
        Center center = centerRepository.findById(centerNum).get();
        CenterDTO centerDTO = modelMapper.map(center, CenterDTO.class);

        return centerDTO;
    }

    @Override
    public void centerModify(CenterDTO centerDTO) {
        log.info("center Modify Service 진입");

        //center 수정
        Center center = centerRepository.findById(centerDTO.getCenterNum()).orElseThrow();

        center.setCenterBrand(centerDTO.getCenterBrand());
        center.setCenterName(centerDTO.getCenterName());
        center.setCenterPhone(centerDTO.getCenterPhone());
        center.setCenterEmail(centerDTO.getCenterEmail());
        center.setCenterAddress(centerDTO.getCenterAddress());
        center.setCenterCeoName(centerDTO.getCenterCeoName());
        center.setCenterBusinessNum(centerDTO.getCenterBusinessNum());

        centerRepository.save(center);

        log.info("수정 반영된 내용 : " + center);

    }

    @Override
    public void centerDelete(Long centerNum) {

        centerRepository.deleteById(centerNum);

        log.info("삭제된 pk : " + centerNum);

    }

    //가입용
    @Override
    public List<CenterDTO> allCenterList(){
        List<Center> centerList = centerRepository.findAll();
        List<CenterDTO> centerDTOList = new ArrayList<>();
        for (Center center : centerList) {
            CenterDTO centerDTO = modelMapper.map(center, CenterDTO.class);
            centerDTOList.add(centerDTO);
        }
        return centerDTOList;
    }
}
