package com.sixthsense.hexastay.service.impl;

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
}
