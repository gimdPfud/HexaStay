package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.*;

import java.util.List;

public interface AdminService {

    // 어드민 등록
    void insertAdmin(AdminDTO adminDTO);

    // 어드민 목록
    List<AdminDTO> getAdminList();
    AdminDTO getAdmin(Long adminNum);

    // 가입 대기자 목록
    List<AdminDTO> getWaitAdminList();

    // 가입 승인
    void setAdminActive(Long adminNum);

    // 회원 가입떄 쓸 로직
    List<CenterDTO> getCenterList (String CenterName);

    // 회원 가입때 쓸 로직2
    List<BranchDTO> getBranchList (String CenterName);
    List<FacilityDTO> getFacilityList (String CenterName);
    List<StoreDTO> getStoreList();

}
