package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.AdminService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
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

    // 리스트 페이지용

    @Override
    public Page<AdminDTO> listAdmin(String email, Pageable pageable) {
        Admin admin = adminRepository.findByAdminEmail(email);
        Page<Admin> adminList = null;

        if (admin.getCompany() != null) {
            Long companyNum = admin.getCompany().getCompanyNum();
            adminList = adminRepository.findByCompany_CompanyNum(companyNum, pageable);
        } else if (admin.getStore() != null) {
            Long storeNum = admin.getStore().getStoreNum();
            adminList = adminRepository.findByStore_StoreNum(storeNum, pageable);
        }
        return adminList.map(adminOne -> modelMapper.map(adminOne, AdminDTO.class));
    }

    @Override
    public Page<AdminDTO> listAdminSearch(String email, String type, String keyword, Pageable pageable) {
        try {
            Admin admin = adminRepository.findByAdminEmail(email);
            if (admin == null) {
                throw new NoSuchElementException("Admin not found");
            }

            Page<Admin> adminList;
            if (admin.getCompany() != null) {
                Long companyNum = admin.getCompany().getCompanyNum();
                adminList = adminRepository.listPageAdminSearch(companyNum, type, keyword, pageable);
            } else if (admin.getStore() != null) {
                Long storeNum = admin.getStore().getStoreNum();
                adminList = adminRepository.listPageAdminStoreSearch(storeNum, type, keyword, pageable);
            } else {
                throw new IllegalStateException("Admin has no company or store");
            }

            return adminList.map(adminOne -> modelMapper.map(adminOne, AdminDTO.class));
        } catch (Exception e) {
            log.error("Error in listAdminSearch: ", e);
            throw e;
        }
    }


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

        //사번 자동증분
        String yearPrefix = String.valueOf(Year.now().getValue()).substring(2);
        String maxEmpNum = adminRepository.findMaxEmpNumStartingWith(yearPrefix + "%");
        int nextSeq = 1;
            int lastSeq = Integer.parseInt(maxEmpNum.substring(2)); // 뒤 6자리
            nextSeq = lastSeq + 1;
            String employeeNum = yearPrefix + String.format("%06d", nextSeq);
        adminDTO.setAdminEmployeeNum(employeeNum);


        // 이제 저변환하고 저장
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        if (adminDTO.getCompanyNum() != null) {
            admin.getCompany().setCompanyNum(adminDTO.getCompanyNum());
        } else if (adminDTO.getStoreNum() != null) {
            admin.getStore().setStoreNum(adminDTO.getStoreNum());
        }

        adminRepository.save(admin);
    }


    //가입대기 리스트
    public List<AdminDTO> getWaitAdminList() {
        log.info("=== getWaitAdminList 시작 ===");
        List<Admin> adminList = adminRepository.findByAdminActive("INACTIVE");
        log.info("INACTIVE 상태의 회원 수: {}", adminList.size());
        
        if (adminList.isEmpty()) {
            log.warn("INACTIVE 상태의 회원이 없습니다. 데이터베이스 쿼리 결과를 확인해주세요.");
            return new ArrayList<>();
        }
        
        List<AdminDTO> adminDTOList = new ArrayList<>();
        
        // 현재 로그인한 관리자의 이메일을 가져옴
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("현재 로그인한 관리자 이메일: {}", currentAdminEmail);
        
        Admin currentAdmin = adminRepository.findByAdminEmail(currentAdminEmail);
        if (currentAdmin == null) {
            log.warn("현재 로그인한 관리자를 찾을 수 없습니다.");
            return adminDTOList;
        }
        
        String role = currentAdmin.getAdminRole().toUpperCase(); // 대문자로 변환
        log.info("현재 관리자의 역할: {}", role);
        log.info("현재 관리자의 회사: {}", currentAdmin.getCompany() != null ? currentAdmin.getCompany().getCompanyNum() : "없음");
        log.info("현재 관리자의 스토어: {}", currentAdmin.getStore() != null ? currentAdmin.getStore().getStoreNum() : "없음");
        
        // 계층별 조회 권한 설정
        List<String> allowedRoles = new ArrayList<>();
        switch (role) {
            case "EXEC":
            case "GM":
            case "MGR":
                allowedRoles.addAll(Arrays.asList("EXEC", "GM", "MGRHEAD", "CREW", "SV", "AGENT", "PARTNER", "SUBMGR", "STAFF"));
                break;
            case "HEAD":
            case "SV":
            case "SUBMGR":
                allowedRoles.addAll(Arrays.asList("HEAD", "SV", "SUBMGR", "CREW", "AGENT", "PARTNER", "STAFF"));
                break;
            default:
                log.warn("권한이 없는 역할입니다: {}", role);
                return adminDTOList;
        }
        log.info("허용된 역할 목록: {}", allowedRoles);
        
        // 현재 관리자의 소속 회사/스토어에 속한 관리자만 필터링
        for (Admin admin : adminList) {
            log.info("처리 중인 회원: {}, 역할: {}, 회사: {}, 스토어: {}", 
                admin.getAdminName(), 
                admin.getAdminRole(),
                admin.getCompany() != null ? admin.getCompany().getCompanyNum() : "없음",
                admin.getStore() != null ? admin.getStore().getStoreNum() : "없음");
                
            if (allowedRoles.contains(admin.getAdminRole().toUpperCase())) { // 대문자로 변환하여 비교
                boolean isSameCompany = currentAdmin.getCompany() != null && admin.getCompany() != null 
                    && currentAdmin.getCompany().getCompanyNum().equals(admin.getCompany().getCompanyNum());
                boolean isSameStore = currentAdmin.getStore() != null && admin.getStore() != null 
                    && currentAdmin.getStore().getStoreNum().equals(admin.getStore().getStoreNum());
                
                if (isSameCompany || isSameStore) {
                    adminDTOList.add(modelMapper.map(admin, AdminDTO.class));
                    log.info("회원 추가됨: {}", admin.getAdminName());
                } else {
                    log.info("회원 제외됨 (소속 불일치): {}", admin.getAdminName());
                }
            } else {
                log.info("회원 제외됨 (역할 불일치): {}", admin.getAdminName());
            }
        }
        log.info("=== getWaitAdminList 종료, 결과 회원 수: {} ===", adminDTOList.size());
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
        } else if (adminDTO.getAdminProfileMeta().isEmpty() && !admin.getAdminProfileMeta().isEmpty()) {
            adminDTO.setAdminProfileMeta(admin.getAdminProfileMeta());
        }

        adminRepository.save(modelMapper.map(adminDTO, Admin.class));
    }

    // 회원 삭제
    @Override
    public void adminDelete(Long adminNum) throws IOException {
        Admin admin = adminRepository.findById(adminNum).orElseThrow(() -> new NoSuchElementException("해당 직원이 없습니다."));

        if (admin.getAdminProfileMeta() != null && !admin.getAdminProfileMeta().isEmpty()) {
            Path filePath = Paths.get(System.getProperty("user.dir"), admin.getAdminProfileMeta().substring(1));
            Files.deleteIfExists(filePath);
        }
        adminRepository.deleteById(adminNum);
    }

    @Override
    public AdminDTO adminFindEmail(String email) {
        Admin admin = adminRepository.findByAdminEmail(email);
        return admin != null ? modelMapper.map(admin, AdminDTO.class) : null;
    }

    @Override
    public List<CompanyDTO> insertSelectCompany(String companyType) {
        List<Company> companyList = companyRepository.findByCompanyType(companyType);
        List<CompanyDTO> companyDTOList = new ArrayList<>();
        for (Company company : companyList) {
            CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);
            companyDTOList.add(companyDTO);
        }
        return companyDTOList;
    }

    @Override
    public List<CompanyDTO> insertSelectList(Long centerNum, String adminChoice) {
        List<Company> companyList =
                switch (adminChoice) {
                    case "branch" -> companyRepository.findByCompanyTypeAndCompanyParent("branch", centerNum);
                    case "facility" -> companyRepository.findByCompanyTypeAndCompanyParent("facility", centerNum);
                    case "store" -> companyRepository.findByCompanyParent(centerNum);
                    default -> new ArrayList<>();
                };
        return companyList.stream().map(company -> modelMapper.map(company, CompanyDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<StoreDTO> insertStoreList(Long branchFacilityNum) {
        List<Store> storeList = storeRepository.findByCompanyNum(branchFacilityNum);
        log.info(storeList.stream().toList());
        return storeList.stream().map(store -> modelMapper.map(store, StoreDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Admin createAdminEmployeeNum(Admin admin) {
        String newEmpNum = generateNextEmployeeNum();
        admin.setAdminEmployeeNum(newEmpNum);
        return adminRepository.save(admin);
    }

    public String generateNextEmployeeNum() {
        String yearPrefix = String.valueOf(Year.now().getValue()).substring(2);
        String maxEmpNum = adminRepository.findMaxEmpNumStartingWith(yearPrefix + "%");
        int nextSeq = 1;
        if (maxEmpNum != null) {
            int lastSeq = Integer.parseInt(maxEmpNum.substring(2)); // 뒤 6자리
            nextSeq = lastSeq + 1;
            String employeeNum = yearPrefix + String.format("%06d", nextSeq);
        }
        return yearPrefix + String.format("%06d", nextSeq);
    }

}