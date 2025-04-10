package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.AdminService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final PasswordEncoder passwordEncoder;

    public List<CenterDTO> getCenterList(String centerName) {
        List<CenterDTO> centerDTOList = new ArrayList<>();
        List<Center> centerList = centerRepository.findByCenterName(centerName);

        for (Center center : centerList) {
            centerDTOList.add(modelMapper.map(center, CenterDTO.class));
        }
        return centerDTOList;
    }

    // 회원 등록
    public void insertAdmin(AdminDTO adminDTO) throws IOException {

        adminDTO.setAdminPassword(passwordEncoder.encode(adminDTO.getAdminPassword()));

        if (adminDTO.getAdminProfile() != null && !adminDTO.getAdminProfile().isEmpty()) {
            String fileOriginalName = adminDTO.getAdminProfile().getOriginalFilename();
            String fileFirstName = adminDTO.getAdminEmployeeNum() + "_" + adminDTO.getAdminName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            adminDTO.setAdminProfileMeta("/profile/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "profile/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "profile/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            adminDTO.getAdminProfile().transferTo(uploadPath.toFile());

        }

        Admin admin = modelMapper.map(adminDTO, Admin.class);
        adminRepository.save(admin);
    }

    //어드민 리스트
    public Page<AdminDTO> getAdminList(Pageable pageable) {

        Page<Admin> adminList = adminRepository.findAllWithJoins(pageable);
        return adminList.map(admin -> {
            AdminDTO dto = modelMapper.map(admin, AdminDTO.class);
            if (admin.getCenter() != null)
                dto.setCenterName(admin.getCenter().getCenterName());
            if (admin.getBranch() != null)
                dto.setBranchName(admin.getBranch().getBranchName());
            if (admin.getFacility() != null)
                dto.setFacilityName(admin.getFacility().getFacilityName());
            if (admin.getStore() != null)
                dto.setStoreName(admin.getStore().getStoreName());
            return dto;
        });
    }


    //가입대기 리스트
    public List<AdminDTO> getWaitAdminList() {
        List<Admin> adminList = adminRepository.findByAdminActive(false);
        List<AdminDTO> adminDTOList = new ArrayList<>();
        for (Admin admin : adminList) {
            adminDTOList.add(modelMapper.map(admin, AdminDTO.class));
        }
        return adminDTOList;
    }

    //가입 승인
    public void setAdminActive(Long adminNum) {
        Admin admin = adminRepository.findByAdminNum(adminNum);
        admin.setAdminActive(true);
        adminRepository.save(admin);
    }

    public AdminDTO getAdmin(Long adminNum) {
        Admin admin = adminRepository.findByAdminNum(adminNum);
        return modelMapper.map(admin, AdminDTO.class);
    }


    public List<BranchDTO> getBranchList(Long centerNum) {
        List<BranchDTO> branchDTOList = new ArrayList<>();
        List<Branch> branchList = branchRepository.findByCenter_CenterNum(centerNum);
        for (Branch branch : branchList) {
            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setBranchAddress(branch.getBranchAddress());
            branchDTO.setBranchName(branch.getBranchName());
            branchDTO.setBranchEmail(branch.getBranchEmail());
            branchDTO.setBranchNum(branch.getBranchNum());
            branchDTO.setBranchPhone(branch.getBranchPhone());
            branchDTO.setBranchCeoName(branch.getBranchCeoName());
            branchDTO.setBranchBusinessNum(branch.getBranchBusinessNum());
            branchDTO.setCreateDate(branch.getCreateDate());
            branchDTO.setModifyDate(branch.getModifyDate());
            branchDTO.setCenterNum(branch.getCenter().getCenterNum());
            branchDTO.setCenterName(branch.getCenter().getCenterName());

            branchDTOList.add(branchDTO);


        }
        return branchDTOList;
    }


    public List<FacilityDTO> getFacilityList(Long centerNum) {
        List<FacilityDTO> facilityDTOList = new ArrayList<>();
        List<Facility> facilityList = facilityRepository.findByCenter_CenterNum(centerNum);
        for (Facility facility : facilityList) {
            facilityDTOList.add(modelMapper.map(facility, FacilityDTO.class));
        }
        return facilityDTOList;
    }

    public List<StoreDTO> getStoreList() {

        List<StoreDTO> storeDTOList = new ArrayList<>();
        List<Store> storeList = storeRepository.findAll();
        for (Store store : storeList) {
            StoreDTO storeDTO = new StoreDTO();
            modelMapper.map(store, storeDTO);
            storeDTOList.add(storeDTO);
        }
        return storeDTOList;
    }


    // 리스트 검색용
    public Page<AdminDTO> getAdminSearch(String type, String keyword, Pageable pageable) {
            Page<Admin> adminList = adminRepository.findByAdminSearchKeyword(type, keyword, pageable);
        return adminList.map(admin -> modelMapper.map(admin, AdminDTO.class));
    }

}