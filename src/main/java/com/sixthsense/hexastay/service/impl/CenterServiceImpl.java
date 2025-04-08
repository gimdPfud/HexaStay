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

        //center 등록 (DTO를 Entity로 변환해서 Entity에 담고)
        Center center = modelMapper.map(centerDTO, Center.class);
        //Entity에 저장
        center = centerRepository.save(center);

        log.info("centerDTO를 Entity로 변환 완료 : " + center);
    }

    @Override
    public Page<CenterDTO> centerList(Pageable pageable) {
        log.info("center List Service 진입");

        //center 목록 (findAll로 전체 목록 불러와서 페이징 후 Entity에 저장)
        Page<Center> centerPage = centerRepository.findAll(pageable);
        //Entity를 DTO로 변환 후 저장(페이징 된 상태로)
        Page<CenterDTO> centerDTOS = centerPage.map(center -> modelMapper.map(center, CenterDTO.class));
        //DTO를 반환
        return centerDTOS;
    }

    @Override
    public CenterDTO centerRead(Long centerNum) {
        log.info("center Read Service 진입");

        //center 상세보기 (pk로 Entity 찾기)
        Center center = centerRepository.findById(centerNum).orElse(null);

        if(center == null){
            return null;
        }

        //Entity를 DTO로 변환 후 DTO 반환
        CenterDTO centerDTO = modelMapper.map(center, CenterDTO.class);
        //DTO를 반환
        return centerDTO;
    }


    @Override
    public void centerModify(CenterDTO centerDTO) {
        log.info("center Modify Service 진입");

        //center 수정(DTO에서 pk로 Entity 찾기)
        Center center = centerRepository.findById(centerDTO.getCenterNum()).orElseThrow();

        //수정 내용 Entity에 set 해주기
        center.setCenterBrand(centerDTO.getCenterBrand());
        center.setCenterName(centerDTO.getCenterName());
        center.setCenterPhone(centerDTO.getCenterPhone());
        center.setCenterEmail(centerDTO.getCenterEmail());
        center.setCenterAddress(centerDTO.getCenterAddress());
        center.setCenterCeoName(centerDTO.getCenterCeoName());
        center.setCenterBusinessNum(centerDTO.getCenterBusinessNum());

        //Entity에 저장
        centerRepository.save(center);

        log.info("수정 반영된 내용 : " + center);
    }

    @Override
    public void centerDelete(Long centerNum) {
        log.info("center Delete Service 진입");

        //center 삭제 (pk로 Entity 찾아서 삭제)
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

    // 본사명 조회용
    @Override
    public Page<CenterDTO> companyName(String keyword, Pageable pageable){
        //검색하면 본사명을 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerList = centerRepository.findByCenterNameContaining(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<CenterDTO> centerDTOList = centerList.map(center -> modelMapper.map(center, CenterDTO.class));
        //Page타입 DTO를 반환
        return centerDTOList;
    }

    // 브랜드명 조회용
    @Override
    public Page<CenterDTO> brandName(String keyword, Pageable pageable){
        //검색하면 브랜드명을 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerPageList = centerRepository.findByCenterBrand(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<CenterDTO> centerDTOList = centerPageList.map(center -> modelMapper.map(center, CenterDTO.class));
        //Page타입 DTO를 반환
        return centerDTOList;
    }

    //사업자등록번호 조회용
    @Override
    public Page<CenterDTO> centerBusinessNum(String keyword, Pageable pageable) {
        //검색하면 사업자등록번호를 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerPageList = centerRepository.findByCenterBusinessNum(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<CenterDTO> centerDTOList = centerPageList.map(center -> modelMapper.map(center, CenterDTO.class));
        //Page타입 DTO를 반환
        return centerDTOList;
    }

    @Override
    public List<String> findDistinctCenterBrand() {

        return centerRepository.findDistinctCenterBrand();
    }

    @Override
    public List<String> findDistinctCenterName() {

        return centerRepository.findDistinctCenterName();
    }


}
