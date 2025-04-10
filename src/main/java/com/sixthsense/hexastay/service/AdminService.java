package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    // 어드민 등록
    void insertAdmin (AdminDTO adminDTO) throws IOException;

    // 어드민 목록
    Page<AdminDTO> getAdminList(Pageable pageable);
    AdminDTO getAdmin(Long adminNum);


    // 가입 대기자 목록
    List<AdminDTO> getWaitAdminList();

    // 가입 승인
    void setAdminActive(Long adminNum);

    // 회원 가입떄 쓸 로직
    List<CenterDTO> getCenterList (String CenterName);

    // 회원 가입때 쓸 로직2
    List<BranchDTO> getBranchList (Long CenterNum);
    List<FacilityDTO> getFacilityList (Long CenterNum);
    List<StoreDTO> getStoreList();


    // 리스트 검색용
    Page<AdminDTO> getAdminSearch(String type, String keyword, Pageable pageable);


}
