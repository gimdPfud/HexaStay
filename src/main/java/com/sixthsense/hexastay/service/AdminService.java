package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;

import java.util.List;

public interface AdminService {



    // 어드민 등록
    void insertAdmin(AdminDTO adminDTO);

    // 회원가입떄 쓸 로직
    List<CenterDTO> getCenterList (String CenterName);

    // 회원가입때 쓸 로직2
    List<BranchDTO> getBranchList (Long centerNum);
    List<FacilityDTO> getFacilityList (Long branchNum);

}
