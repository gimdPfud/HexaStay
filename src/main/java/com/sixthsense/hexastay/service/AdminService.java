package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;

import java.util.List;

public interface AdminService {


    // 회원가입떄 쓸 로직
    List<CenterDTO> getCenterList (String CenterName);

    List<BranchDTO> getBranchList (String BranchName);
    List<FacilityDTO> getFacilityList (String FacilityName);

}
