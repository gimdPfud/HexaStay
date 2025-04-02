package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.entity.Facility;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.BranchRepository;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.repository.FacilityRepository;
import com.sixthsense.hexastay.service.AdminService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Service
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final CenterRepository centerRepository;
    private final BranchRepository branchRepository;
    private final FacilityRepository facilityRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public List<CenterDTO> getCenterList(String centerName) {
        List<CenterDTO> centerDTOList = new ArrayList<>();
        List<Center> centerList = centerRepository.findByCenterName(centerName);

        for (Center center : centerList) {
            centerDTOList.add(modelMapper.map(center, CenterDTO.class));
        }
        return centerDTOList;
    }

    // 회원 등록
    public void insertAdmin(AdminDTO adminDTO) {
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        adminRepository.save(admin);
    }

    public List<BranchDTO> getBranchList(String CenterName) {

        List<BranchDTO> branchDTOList = new ArrayList<>();
        List<Branch> branchList = branchRepository.findByCenter_CenterName(centerName);
        for (Branch branch : branchList) {
            branchDTOList.add(modelMapper.map(branch, BranchDTO.class));
        }
        return branchDTOList;
    }

    public List<FacilityDTO> getFacilityList(String CenterName) {
        List<FacilityDTO> facilityDTOList = new ArrayList<>();
        List<Facility> facilityList = facilityRepository.findByCenter_CenterName(centerName);
        for (Facility facility : facilityList) {
            facilityDTOList.add(modelMapper.map(facility, FacilityDTO.class));
        }
        return facilityDTOList;

    }
}
