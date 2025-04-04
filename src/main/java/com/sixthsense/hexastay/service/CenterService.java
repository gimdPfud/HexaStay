package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CenterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CenterService {

    //center 등록
    public void centerInsert(CenterDTO centerDTO);

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

    // 브랜드로 조회
    Page<CenterDTO> brandName(String keyword, Pageable pageable);

}
