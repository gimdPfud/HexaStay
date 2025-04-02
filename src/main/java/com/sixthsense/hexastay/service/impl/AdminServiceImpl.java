package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.entity.Facility;
import com.sixthsense.hexastay.repository.*;
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
    private final StoreRepository storeRepository;
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

    public List<BranchDTO> getBranchList(String centerName) {
        List<BranchDTO> branchDTOList = new ArrayList<>();
        List<Branch> branchList = branchRepository.findByCenter_CenterName(centerName);
        for (Branch branch : branchList) {
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setBranchAddress(branch.getBranchAddress());
        branchDTO.setBranchName(branch.getBranchName());
        branchDTO.setBranchEmail(branch.getBranchEmail());
        branchDTO.setBranchNum(branch.getBranchNum());
        branchDTO.setBranchPhone(branch.getBranchPhone());
        branchDTO.setBranchCeoName(branch.getBranchCeoName());
        branchDTO.setCenterBusinessNum(branch.getCenterBusinessNum());
        branchDTO.setCreateDate(branch.getCreateDate());
        branchDTO.setModifyDate(branch.getModifyDate());
        branchDTO.setCenterNum(branch.getCenter().getCenterNum());
        branchDTO.setCenterName(branch.getCenter().getCenterName());

        branchDTOList.add(branchDTO);


        }
        return branchDTOList;
    }


    public List<FacilityDTO> getFacilityList(String centerName) {
        List<FacilityDTO> facilityDTOList = new ArrayList<>();
        List<Facility> facilityList = facilityRepository.findByCenter_CenterName(centerName);
        for (Facility facility : facilityList) {
            facilityDTOList.add(modelMapper.map(facility, FacilityDTO.class));
        }
        return facilityDTOList;
    }

//    public List<StoreDTO> getStoreList(String branchName, String facilityName) {
//
//        List<StoreDTO> storeDTOList = new ArrayList<>();
//        if (branchName != null) {
//            storeRepository.findBy_(branchName);
//        } else {
//            facilityRepository.findByFacilityName(facilityName);
//            return
//        }
//
//        return null;
//    }
}
