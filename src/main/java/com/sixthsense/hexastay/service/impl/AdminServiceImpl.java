package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Service
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

    public List<BranchDTO> getBranchList(String branchName) {
        List<BranchDTO> branchDTOList = new ArrayList<>();
        List<Branch> branchList = branchRepository.findByBranchName(branchName);
        for (Branch branch : branchList) {
            branchDTOList.add(modelMapper.map(branch, BranchDTO.class));
        }
        return branchDTOList;
    }

    public List<FacilityDTO> getFacilityList(String facilityName) {
        List<FacilityDTO> facilityDTOList = new ArrayList<>();
        List<Facility> facilityList = facilityRepository.findByFacilityName(facilityName);
        for (Facility facility : facilityList) {
            facilityDTOList.add(modelMapper.map(facility, FacilityDTO.class));
        }
        return facilityDTOList;

    }
}
