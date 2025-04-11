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
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;


    // 가입
    @Override
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


    //리스트
    @Override
    public Page<AdminDTO> list(Pageable pageable) {

        Page<Admin> adminList = adminRepository.findAll(pageable);
        Page<AdminDTO> adminDTOList = adminList.map(admin -> modelMapper.map(admin, AdminDTO.class));

        return adminDTOList;
    }

    // 리스트 검색용
    @Override
    public Page<AdminDTO> searchAdmins(String select, String choice, String keyword, Pageable pageable) {
        Page<Admin> adminList = adminRepository.listAdminSearch(select, choice, keyword, pageable);
        Page<AdminDTO> adminDTOList = adminList.map(admin -> modelMapper.map(admin, AdminDTO.class));
        return adminDTOList;
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


}