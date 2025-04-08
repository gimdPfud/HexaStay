package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.entity.Facility;
import com.sixthsense.hexastay.repository.BranchRepository;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.repository.FacilityRepository;
import com.sixthsense.hexastay.service.FacilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FacilityServiceImpl implements FacilityService {

    private final CenterRepository centerRepository;
    private final FacilityRepository facilityRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void facilityInsert(FacilityDTO facilityDTO) {
        log.info("Facility Insert Sercive 진입");

        //facility 등록
        Facility facility = modelMapper.map(facilityDTO, Facility.class);
        facilityRepository.save(facility);

        log.info("facilityDTO를 Entity로 변환 완료 : " + facility);
    }

    @Override
    public Page<FacilityDTO> facilityList(Pageable pageable) {
        log.info("Facility List Sercive 진입");

        //facility 목록
        Page<Facility> facilityPage = facilityRepository.findAll(pageable);
        Page<FacilityDTO> facilityDTOS = facilityPage.map(facility -> modelMapper.map(facility, FacilityDTO.class));

        return facilityDTOS;
    }

    @Override
    public FacilityDTO facilityRead(Long facilityNum) {
        log.info("Facility Read Sercive 진입");

        //Facility 상세보기
        Facility facility = facilityRepository.findById(facilityNum).get();
        FacilityDTO facilityDTO = modelMapper.map(facility, FacilityDTO.class);

        return facilityDTO;
    }

    @Override
    public void facilityModify(FacilityDTO facilityDTO) {
        log.info("Facility Modify Sercive 진입");

        //Facility 수정
        Facility facility = facilityRepository.findById(facilityDTO.getFacilityNum()).orElseThrow();

        facility.setFacilityNum(facilityDTO.getFacilityNum());
        facility.setFacilityName(facilityDTO.getFacilityName());
        facility.setFacilityPhone(facilityDTO.getFacilityPhone());
        facility.setFacilityEmail(facilityDTO.getFacilityEmail());
        facility.setFacilityAddress(facilityDTO.getFacilityAddress());
        facility.setFacilityCeoName(facilityDTO.getFacilityCeoName());
        facility.setFacilityBusinessNum(facilityDTO.getFacilityBusinessNum());

        facilityRepository.save(facility);

        log.info("수정 반영된 내용 : " + facility);
    }

    @Override
    public void facilityDelete(Long facilityNum) {

        facilityRepository.deleteById(facilityNum);

        log.info("삭제된 pk : " + facilityNum);
    }

    @Override
    public Page<FacilityDTO> companyName(String keyword, Pageable pageable) {
        Page<Facility> facilityList = facilityRepository.findByFacilityNameContaining(keyword, pageable);
        Page<FacilityDTO> facilityDTOList = facilityList.map(facility -> modelMapper.map(facility, FacilityDTO.class));

        return facilityDTOList;
    }

    @Override
    public Page<FacilityDTO> brandName(String keyword, Pageable pageable) {
        Center center = centerRepository.findByCenterBrand(keyword);
        Long centerNum = center.getCenterNum();
        Page<Facility> facilityPage = facilityRepository.findByCenter_CenterNum(centerNum, pageable);
        Page<FacilityDTO> facilityDTOS = facilityPage.map(facility -> modelMapper.map(facility, FacilityDTO.class));

        return facilityDTOS;
    }

    @Override
    public Page<FacilityDTO> facilityBusinessNum(String keyword, Pageable pageable) {
        //검색하면 사업자등록번호를 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Facility> facilityPage = facilityRepository.findByCenter_CenterNameContaining(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<FacilityDTO> facilityDTOS = facilityPage.map(facility -> modelMapper.map(facility, FacilityDTO.class));
        //Page타입 DTO를 반환
        return facilityDTOS;
    }
}
