package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.entity.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FacilityService {

    //facility 등록
    public void facilityInsert(FacilityDTO facilityDTO);

    //facility 목록
    public Page<FacilityDTO> facilityList(Pageable pageable);

    //facility 상세보기
    public FacilityDTO facilityRead(Long facilityNum);

    //facility 수정
    public void facilityModify(FacilityDTO facilityDTO);

    //facility 삭제
    public void facilityDelete(Long facilityNum);

    //조회용
    public List<FacilityDTO> allFacilityList();

    //조직명으로 조회
    public Page<FacilityDTO> companyName(String keyword, Pageable pageable);

    //브랜드명으로 조회
    public Page<FacilityDTO> brandName(String keyword, Pageable pageable);

    //사업자번호로 조회
    public Page<FacilityDTO> facilityBusinessNum(String keyword, Pageable pageable);
}
