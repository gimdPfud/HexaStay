package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Salaries;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.SalariesRepository;
import com.sixthsense.hexastay.service.SalariesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SalariesServiceImpl implements SalariesService {

    private final SalariesRepository salariesRepository;
    private final ModelMapper modelMapper;
    private final AdminRepository adminRepository;


    // 월급입력에 쓸 직원 리스트
    @Override
    public Page<AdminDTO> getSalarAdminList(String email, Pageable pageable) {
        Admin admin = adminRepository.findByAdminEmail(email);
        List<Admin> adminList = new ArrayList<>();

        if (admin.getCompany().getCompanyNum() != null) {
            Long companyNum = admin.getCompany().getCompanyNum();
            adminList.addAll(adminRepository.findBySalariesCompany(companyNum));

        } else if (admin.getStore().getStoreNum() != null) {
            Long storeNum = admin.getStore().getStoreNum();
            adminList.addAll(adminRepository.findBySalariesStore(storeNum));
        }
        Page<Admin> adminPageList = new PageImpl<>(adminList, pageable, adminList.size());
        return adminPageList.map(asi->modelMapper.map(asi, AdminDTO.class));
    }


    // 리스트 뷰에 쓸 직원 리스트
    @Override
    public Page<SalariesDTO> getSalariesList(String email, Pageable pageable) {
        Admin admin = adminRepository.findByAdminEmail(email);
        String role = admin.getAdminRole();
        Long companyNum = null;
        Long storeNum = null;

        if (admin.getCompany().getCompanyNum() != null) {
            companyNum = admin.getCompany().getCompanyNum();
        } else if (admin.getStore().getStoreNum() != null) {
            storeNum = admin.getStore().getStoreNum();
        }

        List<Salaries> oriSalariesList = new ArrayList<>();

        // 본사 계열
        if (role.equals("EXEC") || role.equals("HEAD") || role.equals("CREW")) {
            oriSalariesList = salariesRepository.findHeadOfficeSalaries(email, role, companyNum);
        }
        // 지사/지점 계열
        else if (role.equals("GM") || role.equals("SV") || role.equals("AGENT") || role.equals("PARTNER")) {
            oriSalariesList = salariesRepository.findBranchSalaries(email, role, companyNum);
        }
        // 스토어 계열
        else if (role.equals("MGR") || role.equals("SUBMGR") || role.equals("STAFF")) {
            oriSalariesList = salariesRepository.findStoreSalaries(email, role, storeNum);
        }

        if (oriSalariesList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Page<Salaries> salariesList = new PageImpl<>(oriSalariesList, pageable, oriSalariesList.size());
        Page<SalariesDTO> salariesDTOList = salariesList.map(salary -> salary != null ? modelMapper.map(salary, SalariesDTO.class) : null);
        return salariesDTOList;
    }
    
    // 월별 범위로 급여 목록 조회
    @Override
    public Page<SalariesDTO> getSalariesListByMonthRange(String email, YearMonth startMonth, YearMonth endMonth, Pageable pageable) {
        // 기존 목록 조회
        Admin admin = adminRepository.findByAdminEmail(email);
        String role = admin.getAdminRole();
        Long companyNum = null;
        Long storeNum = null;

        if (admin.getCompany().getCompanyNum() != null) {
            companyNum = admin.getCompany().getCompanyNum();
        } else if (admin.getStore().getStoreNum() != null) {
            storeNum = admin.getStore().getStoreNum();
        }

        List<Salaries> oriSalariesList = new ArrayList<>();

        // 본사 계열
        if (role.equals("exec") || role.equals("head") || role.equals("crew")) {
            oriSalariesList = salariesRepository.findHeadOfficeSalaries(email, role, companyNum);
        }
        // 지사/지점 계열
        else if (role.equals("gm") || role.equals("sv") || role.equals("agent") || role.equals("partner")) {
            oriSalariesList = salariesRepository.findBranchSalaries(email, role, companyNum);
        }
        // 스토어 계열
        else if (role.equals("mgr") || role.equals("submgr") || role.equals("staff")) {
            oriSalariesList = salariesRepository.findStoreSalaries(email, role, storeNum);
        }

        if (oriSalariesList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        // 월별 필터링 적용
        List<Salaries> filteredList = oriSalariesList.stream()
            .filter(salary -> {
                YearMonth salaryMonth = salary.getSalDate();
                // null 체크와 월별 범위 체크
                return salaryMonth != null && 
                       (salaryMonth.compareTo(startMonth) >= 0 && 
                        salaryMonth.compareTo(endMonth) <= 0);
            })
            .collect(Collectors.toList());
        
        if (filteredList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredList.size());
        List<Salaries> pageContent = filteredList.subList(start, end);
        
        Page<Salaries> salariesList = new PageImpl<>(pageContent, pageable, filteredList.size());
        Page<SalariesDTO> salariesDTOList = salariesList.map(salary -> 
            salary != null ? modelMapper.map(salary, SalariesDTO.class) : null
        );
        
        return salariesDTOList;
    }


    @Override
    public void postSalaries(SalariesDTO salariesDTO) {
        Salaries salaries = modelMapper.map(salariesDTO, Salaries.class);

        Admin admin = adminRepository.findByAdminNum(salariesDTO.getAdminDTO().getAdminNum());
        salaries.setAdmin(admin);
        
        salariesRepository.save(salaries);
    }
}