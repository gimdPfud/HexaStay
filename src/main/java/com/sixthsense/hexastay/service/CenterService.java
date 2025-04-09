package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CenterService {

    //center 등록
    public void centerInsert(CenterDTO centerDTO) throws IOException;

    //center 목록
    public Page<CenterDTO> centerList(Pageable pageable);

    //center 상세보기
    public CenterDTO centerRead(Long centerNum);

    //center 수정
    public void centerModify(CenterDTO centerDTO);

    //center 삭제
    public void centerDelete(Long centerNum);

    //가입용, 조회용
    public List<CenterDTO> allCenterList();

    //조직명으로 조회
    Page<CenterDTO> companyName(String keyword, Pageable pageable);

    //브랜드로 조회
    Page<CenterDTO> brandName(String keyword, Pageable pageable);

    //사업자등록번호로 조회
    Page<CenterDTO> centerBusinessNum(String keyword, Pageable pageable);

    //브랜드 목록 중복없이 가져오기
    List<String> findDistinctCenterBrand();

    //본사명 목록 중복없이 가져오기
    List<String> findDistinctCenterName();

}
