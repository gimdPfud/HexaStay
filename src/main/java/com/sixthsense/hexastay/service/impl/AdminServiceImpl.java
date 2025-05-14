package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.AdminService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
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
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(context -> context.getSource() != null);

        modelMapper.createTypeMap(AdminDTO.class, Admin.class)
                .addMappings(mapper -> {
                    mapper.skip(Admin::setCompany);
                    mapper.skip(Admin::setStore);
                });

        modelMapper.typeMap(Admin.class, AdminDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getCompany().getCompanyNum(), AdminDTO::setCompanyNum);
            mapper.map(src -> src.getCompany().getCompanyName(), AdminDTO::setCompanyName);
        });

    }

    // 리스트 페이지용

    @Override
    public Page<AdminDTO> listAdmin(String email, Pageable pageable) {
        Admin admin = adminRepository.findByAdminEmail(email);
        Page<Admin> adminList = null;

        // SUPERADMIN인 경우 모든 직원 목록을 볼 수 있음
        if (admin.getAdminRole().equals("SUPERADMIN")) {
            adminList = adminRepository.findAll(pageable);
        } else if (admin.getCompany() != null) {
            Long companyNum = admin.getCompany().getCompanyNum();
            adminList = adminRepository.findByCompany_CompanyNum(companyNum, pageable);
        } else if (admin.getStore() != null) {
            Long storeNum = admin.getStore().getStoreNum();
            adminList = adminRepository.findByStore_StoreNum(storeNum, pageable);
        }

        return adminList.map(adminOne -> {
            AdminDTO dto = modelMapper.map(adminOne, AdminDTO.class);
            if (adminOne.getCompany() != null) {
                dto.setCompanyName(adminOne.getCompany().getCompanyName());
            }
            if (adminOne.getStore() != null) {
                dto.setStoreName(adminOne.getStore().getStoreName());
            }
            return dto;
        });
    }

    @Override
    public Page<AdminDTO> listAdminSearch(String email, String type, String keyword, Pageable pageable) {
        try {
            Admin admin = adminRepository.findByAdminEmail(email);
            if (admin == null) {
                throw new NoSuchElementException("Admin not found");
            }

            Page<Admin> adminList;
            
            // SUPERADMIN인 경우 모든 직원을 대상으로 검색
            if (admin.getAdminRole().equals("SUPERADMIN")) {
                // 검색 조건이 없으면 전체 조회
                if (type == null || type.isEmpty() || keyword == null || keyword.isEmpty()) {
                    adminList = adminRepository.findAll(pageable);
                } else {
                    // 전체 직원 대상 검색 쿼리 실행
                    adminList = adminRepository.searchAllAdmins(type, keyword, pageable);
                }
            } else if (admin.getCompany() != null) {
                Long companyNum = admin.getCompany().getCompanyNum();
                adminList = adminRepository.listPageAdminSearch(companyNum, type, keyword, pageable);
            } else if (admin.getStore() != null) {
                Long storeNum = admin.getStore().getStoreNum();
                adminList = adminRepository.listPageAdminStoreSearch(storeNum, type, keyword, pageable);
            } else {
                throw new IllegalStateException("Admin has no company or store");
            }

            return adminList.map(adminOne -> {
                AdminDTO dto = modelMapper.map(adminOne, AdminDTO.class);
                if (adminOne.getCompany() != null) {
                    dto.setCompanyName(adminOne.getCompany().getCompanyName());
                }
                if (adminOne.getStore() != null) {
                    dto.setStoreName(adminOne.getStore().getStoreName());
                }
                return dto;
            });
        } catch (Exception e) {
            log.error("Error in listAdminSearch: ", e);
            throw e;
        }
    }


    // 가입
    @Override
    public void insertAdmin(AdminDTO adminDTO) throws IOException {

        //사번 자동증분
        String yearPrefix = String.valueOf(Year.now().getValue()).substring(2);
        String maxEmpNum = adminRepository.findMaxEmpNumStartingWith(yearPrefix + "%");
        int nextSeq = 1;
        if (maxEmpNum != null) {
            int lastSeq = Integer.parseInt(maxEmpNum.substring(2)); // 뒤 6자리
            nextSeq = lastSeq + 1;
        }
        String employeeNum = yearPrefix + String.format("%06d", nextSeq);
        adminDTO.setAdminEmployeeNum(employeeNum);

        if (adminDTO.getAdminProfile() != null && !adminDTO.getAdminProfile().isEmpty()) {
            String fileOriginalName = adminDTO.getAdminProfile().getOriginalFilename();
            String fileFirstName = adminDTO.getAdminEmployeeNum() + "_" + adminDTO.getAdminName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;


            // 프로필 메타 경로 설정
            String profileMetaPath = "/profile/" + fileName;

            // 파일 저장
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "profile");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            adminDTO.getAdminProfile().transferTo(filePath.toFile());

            // Admin 엔티티 생성 전에 메타 경로 설정
            adminDTO.setAdminProfileMeta(profileMetaPath);
        }


        // 이제 변환하고 저장
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        if (adminDTO.getCompanyNum() != null) {
            admin.setCompany(companyRepository.findById(adminDTO.getCompanyNum()).orElseThrow(() -> new EntityNotFoundException("Company 존재하지 않음 : " + adminDTO.getCompanyNum())));
        } else if (adminDTO.getStoreNum() != null) {
            admin.setStore(storeRepository.findById(adminDTO.getStoreNum()).orElseThrow(() -> new EntityNotFoundException("Store 존재하지 않음 : " + adminDTO.getStoreNum())));
        }

        // 프로필 메타 경로가 제대로 설정되었는지 확인
        adminRepository.save(admin);
    }


    //가입대기 리스트
    public List<AdminDTO> getWaitAdminList() {
        List<Admin> adminList = adminRepository.findByAdminActive("INACTIVE");
        
        if (adminList.isEmpty()) {
            log.warn("INACTIVE 상태의 회원이 없습니다.");
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

        // 계층별 조회 권한 설정
        List<String> allowedRoles = new ArrayList<>();
        switch (role) {
            case "SUPERADMIN":
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
    @Transactional
    public void adminUpdate(AdminDTO adminDTO) throws IOException {
        Admin admin = adminRepository.findById(adminDTO.getAdminNum())
                .orElseThrow(() -> new NoSuchElementException("해당 직원이 없습니다."));

        // 새로운 사진이 있으면 처리
        if (adminDTO.getAdminProfile() != null && !adminDTO.getAdminProfile().isEmpty()) {
            log.info("새 프로필 사진 업데이트 시작");
            
            // 기존 사진이 있으면 삭제
            if (admin.getAdminProfileMeta() != null && !admin.getAdminProfileMeta().isEmpty()) {
                try {
                    Path oldFilePath = Paths.get(System.getProperty("user.dir"), admin.getAdminProfileMeta().substring(1));
                    log.info("기존 프로필 사진 삭제 시도: {}", oldFilePath);
                    
                    // 파일 존재 여부 확인
                    boolean fileExists = Files.exists(oldFilePath);
                    log.info("파일 존재 여부: {}", fileExists);
                    
                    if (fileExists) {
                        boolean deleted = Files.deleteIfExists(oldFilePath);
                        log.info("파일 삭제 성공 여부: {}", deleted);
                        
                        if (!deleted) {
                            // 파일 삭제에 실패한 경우, 추가 시도
                            try {
                                java.io.File file = oldFilePath.toFile();
                                boolean forcedDelete = file.delete();
                                log.info("강제 삭제 시도 결과: {}", forcedDelete);
                            } catch (Exception e) {
                                log.error("강제 삭제 실패: {}", e.getMessage());
                            }
                        }
                    } else {
                        log.warn("삭제할 파일이 존재하지 않습니다: {}", oldFilePath);
                    }
                } catch (IOException e) {
                    log.error("기존 프로필 사진 삭제 실패: {}", e.getMessage(), e);
                }
            }

            String fileOriginalName = adminDTO.getAdminProfile().getOriginalFilename();
            String fileFirstName = admin.getAdminEmployeeNum() + "_" + adminDTO.getAdminName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            // 프로필 메타 경로 설정
            String profileMetaPath = "/profile/" + fileName;
            log.info("새 프로필 메타 경로: {}", profileMetaPath);
            
            // 파일 저장
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "profile");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            log.info("새 프로필 사진 저장 경로: {}", filePath);
            
            try {
                adminDTO.getAdminProfile().transferTo(filePath.toFile());
                log.info("새 프로필 사진 저장 성공");
                admin.setAdminProfileMeta(profileMetaPath);
                log.info("프로필 메타 정보 업데이트 완료");
            } catch (IOException e) {
                log.error("새 프로필 사진 저장 실패: {}", e.getMessage(), e);
            }
        }

        // 나머지 필드 업데이트
        admin.setAdminAddress(adminDTO.getAdminAddress());
        admin.setAdminPhone(adminDTO.getAdminPhone());
        admin.setAdminPosition(adminDTO.getAdminPosition());
        admin.setAdminRole(adminDTO.getAdminRole());
        
        // 이메일, 이름, 주민번호 업데이트 (슈퍼어드민인 경우)
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin currentAdmin = adminRepository.findByAdminEmail(currentAdminEmail);
        
        if (currentAdmin != null && "SUPERADMIN".equalsIgnoreCase(currentAdmin.getAdminRole())) {
            log.info("슈퍼어드민 권한으로 추가 정보 업데이트");
            
            // 이메일 업데이트
            if (adminDTO.getAdminEmail() != null) {
                log.info("이메일 업데이트: {} -> {}", admin.getAdminEmail(), adminDTO.getAdminEmail());
                admin.setAdminEmail(adminDTO.getAdminEmail());
            }
            
            // 이름 업데이트
            if (adminDTO.getAdminName() != null) {
                log.info("이름 업데이트: {} -> {}", admin.getAdminName(), adminDTO.getAdminName());
                admin.setAdminName(adminDTO.getAdminName());
            }
            
            // 주민번호 업데이트
            if (adminDTO.getAdminResidentNum() != null) {
                log.info("주민번호 업데이트");
                admin.setAdminResidentNum(adminDTO.getAdminResidentNum());
            }
        } else {
            // 슈퍼어드민이 아닌 경우 본인의 이름만 수정 가능
            if (admin.getAdminEmail().equals(currentAdminEmail)) {
                admin.setAdminName(adminDTO.getAdminName());
            }
        }

        adminRepository.save(admin);
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
        if(admin==null){
            return null;
        } else if (admin.getCompany()!=null || admin.getAdminRole().toUpperCase().equals("SUPERADMIN")) {
            return modelMapper.map(admin,AdminDTO.class);
        } else if (admin.getStore()!=null) {
            AdminDTO adminDTO = modelMapper.map(admin,AdminDTO.class);
            adminDTO.setStoreNum(admin.getStore().getStoreNum());
            adminDTO.setStoreName(admin.getStore().getStoreName());
            return adminDTO;
        }else {
            return null;
        }
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
        }
        return yearPrefix + String.format("%06d", nextSeq);
    }

    @Override
    @Transactional
    public void updatePassword(String name, String employeeNum, String birth, 
                             String currentPassword, String newPassword) {
        // 1. 본인 확인
        Admin admin = adminRepository.findByAdminNameAndAdminEmployeeNumAndAdminResidentNumStartingWith(
            name, employeeNum, birth
        );
        if (admin == null) {
            throw new IllegalArgumentException("일치하는 관리자 정보가 없습니다.");
        }

        // 2. 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, admin.getAdminPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호로 업데이트
        admin.setAdminPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

}