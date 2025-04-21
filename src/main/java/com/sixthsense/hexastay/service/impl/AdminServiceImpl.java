package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.AdminService;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Builder
@RequiredArgsConstructor
@Service
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

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

        if (adminDTO.getCompanyNum() != null) {
            admin.getCompany().setCompanyNum(adminDTO.getCompanyNum());
        } else if (adminDTO.getStoreNum() != null) {
            admin.getStore().setStoreNum(adminDTO.getStoreNum());
        }

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
        List<Admin> adminList = adminRepository.findByAdminActive("PENDING");
        List<AdminDTO> adminDTOList = new ArrayList<>();
        for (Admin admin : adminList) {
            adminDTOList.add(modelMapper.map(admin, AdminDTO.class));
        }
        return adminDTOList;
    }


    //가입 승인
    public void setAdminActive(Long adminNum) {
        Admin admin = adminRepository.findByAdminNum(adminNum);
        admin.setAdminActive("ACTIVE");
        adminRepository.save(admin);
    }

    @Override
    public AdminDTO adminRead(Long adminNum) {
        Admin admin = adminRepository.findById(adminNum).orElseThrow(() -> new NoSuchElementException("해당 직원이 없습니다."));
        return modelMapper.map(admin, AdminDTO.class);
    }

    @Override
    public void adminUpdate(AdminDTO adminDTO) throws IOException {

        Admin admin = adminRepository.findById(adminDTO.getAdminNum()).orElseThrow(() -> new NoSuchElementException("해당 직원이 없습니다."));

        if (!adminDTO.getAdminProfileMeta().isEmpty()) {
            Path filePath = Paths.get(System.getProperty("user.dir"), admin.getAdminProfileMeta().substring(1));
            Files.deleteIfExists(filePath);

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

        } else if (adminDTO.getAdminProfileMeta().isEmpty() && !admin.getAdminProfileMeta().isEmpty())
        {adminDTO.setAdminProfileMeta(admin.getAdminProfileMeta());}


        adminRepository.save(modelMapper.map(adminDTO, Admin.class));
    }





    // 회원 삭제
    @Override
    public void adminDelete(Long adminNum) throws IOException{
        log.info("어드민넘 ㅋㅋ" + adminNum);
        Admin admin = adminRepository.findById(adminNum).orElseThrow(() -> new NoSuchElementException("해당 직원이 없습니다."));

        if (admin.getAdminProfileMeta() != null && !admin.getAdminProfileMeta().isEmpty()) {
            Path filePath = Paths.get(System.getProperty("user.dir"), admin.getAdminProfileMeta().substring(1));
            Files.deleteIfExists(filePath);
        }
        adminRepository.deleteById(adminNum);
    }

    @Override
    public AdminDTO adminFindEmail(String adminEmail){
        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        return modelMapper.map(admin, AdminDTO.class);
    }

    @Override
    public List<CompanyDTO> insertSelectCompany (String companyType) {
        List<Company> companyList = companyRepository.findByCompanyType(companyType);
        List<CompanyDTO> companyDTOList = new ArrayList<>();
        for (Company company : companyList) {
            CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);
            companyDTOList.add(companyDTO);
        }
        return companyDTOList;
    }

    @Override
    public List<CompanyDTO> insertSelectList (Long centerNum, String adminChoice) {
        List<Company> companyList =
                switch (adminChoice) {
                    case "branch" ->  companyRepository.findByCompanyTypeAndCompanyParent( "branch", centerNum);
                    case "facility" ->  companyRepository.findByCompanyTypeAndCompanyParent("facility", centerNum);
                    case "store" -> companyRepository.findByCompanyParent(centerNum);
                    default -> new ArrayList<>();
                };
        return companyList.stream().map(company -> modelMapper.map(company, CompanyDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<StoreDTO> insertStoreList (Long branchFacilityNum) {
        List<Store> storeList = storeRepository.findByCompanyNum(branchFacilityNum);
        log.info(storeList.stream().toList());
        return storeList.stream().map(store -> modelMapper.map(store, StoreDTO.class)).collect(Collectors.toList());
    }

}