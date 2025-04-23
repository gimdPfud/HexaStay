package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.AdminRepository;

import com.sixthsense.hexastay.repository.CompanyRepository;

import com.sixthsense.hexastay.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public void companyInsert(CompanyDTO companyDTO) throws IOException {

        //프로필 이미지 처리
        if (companyDTO.getCompanyPicture() != null && !companyDTO.getCompanyPicture().isEmpty()) {
            String fileOriginalName = companyDTO.getCompanyPicture().getOriginalFilename();
            String fileFirstName = companyDTO.getCompanyNum() + "_" + companyDTO.getCompanyName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            companyDTO.setCompanyPictureMeta("/company/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "company/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "company/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            companyDTO.getCompanyPicture().transferTo(uploadPath.toFile());
        }

        // 2. 지사/지점일 경우 → 소속 본사로부터 브랜드명 세팅
        if ("branch".equals(companyDTO.getCompanyType()) || "facility".equals(companyDTO.getCompanyType())) {
            Optional<Company> parentCompanyOpt = companyRepository.findById(companyDTO.getCompanyParent());

            if (parentCompanyOpt.isPresent()) {
                companyDTO.setCompanyBrand(parentCompanyOpt.get().getCompanyBrand());
            } else {
                throw new IllegalArgumentException("소속 본사를 찾을 수 없습니다. [id=" + companyDTO.getCompanyParent() + "]");
            }
        }

        //DTO를 Entity로 변환
        Company company = modelMapper.map(companyDTO, Company.class);

        //Entity에 저장
        company = companyRepository.save(company);
        log.info("companyDTO를 Entity로 변환 완료 : " + company);
    }

    @Override
    public List<CompanyDTO> companyList() {     //페이징 없이 상위 10개 회사 조회
        Pageable pageable = PageRequest.of(0, 10); // 페이지 0, size 10으로 고정
        Page<Company> companyPage = companyRepository.findAll(pageable);

        List<CompanyDTO> companyDTOList = new ArrayList<>();

        for (Company company : companyPage) {
            CompanyDTO dto = modelMapper.map(company, CompanyDTO.class);

            // 기본값
            dto.setCompanyParentName("-");
            dto.setBranchName("-");
            dto.setFacilityName("-");

            String type = company.getCompanyType();

            if ("center".equals(type)) {
                // 본사일 경우: 본사명은 자기 이름, 나머지는 "-"
                dto.setCompanyParentName(company.getCompanyName());

            } else if ("branch".equals(type)) {
                // 지사일 경우: 부모 본사 이름을 찾아서 본사명에, 자기 이름은 지사명
                dto.setBranchName(company.getCompanyName());

                if (company.getCompanyParent() != null) {
                    companyRepository.findById(company.getCompanyParent()).ifPresent(parent -> {
                        dto.setCompanyParentName(parent.getCompanyName());
                    });
                }

            } else if ("facility".equals(type)) {
                // 지점일 경우: 부모 본사 이름을 찾아서 본사명에, 자기 이름은 지점명
                dto.setFacilityName(company.getCompanyName());

                if (company.getCompanyParent() != null) {
                    companyRepository.findById(company.getCompanyParent()).ifPresent(parent -> {
                        dto.setCompanyParentName(parent.getCompanyName());
                    });
                }
            }

            companyDTOList.add(dto);
        }

        return companyDTOList;
    }

    // 검색용
    @Override
    public Page<CompanyDTO> companySearchList(String select, String choice, String keyword, Long companyNum, Pageable pageable) {    //검색조건 적용한 회사 목록
        // 소속만 선택하고 검색 조건/키워드가 없을 때
        if ((keyword == null || keyword.trim().isEmpty()) || "전체".equals(select)) {
            return companyRepository.findByCompanyNumOrParentCompanyNum(companyNum, choice, pageable)
                    .map(this::convertToCompanyDTO);
        }

        // 검색 조건 + 키워드가 있을 때
        Page<Company> companyPage = companyRepository.listSelectSearch(select, choice, keyword, companyNum, pageable);
        return companyPage.map(this::convertToCompanyDTO);
    }

    @Override
    public CompanyDTO companyRead(Long companyNum) {
        Company company = companyRepository.findById(companyNum).orElseThrow();
        CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);

        return companyDTO;
    }

    @Override
    public void companyModify(CompanyDTO companyDTO) throws IOException {

        //프로필 이미지 처리
        if (companyDTO.getCompanyPicture() != null && !companyDTO.getCompanyPicture().isEmpty()) {

            Company companyOri = companyRepository.findById(companyDTO.getCompanyNum()).orElseThrow();
            Path filePath = Paths.get(System.getProperty("user.dir"), companyOri.getCompanyPictureMeta().substring(1));
            Files.deleteIfExists(filePath);

            String fileOriginalName = companyDTO.getCompanyPicture().getOriginalFilename();
            String fileFirstName = companyDTO.getCompanyNum() + "_" + companyDTO.getCompanyName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            companyDTO.setCompanyPictureMeta("/company/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "company/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "company/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            companyDTO.getCompanyPicture().transferTo(uploadPath.toFile());
        }

        Company company = modelMapper.map(companyDTO, Company.class);
        companyRepository.save(company);

    }

    @Override
    public void deactivateCompany(Long companyNum) {
        Company company = companyRepository.findById(companyNum)
                .orElseThrow(() -> new RuntimeException("해당 지사를 찾을 수 없습니다."));

        // 지사 상태 비활성화
        company.setCompanyStatus("INACTIVE");
        companyRepository.save(company);

        // 관련 관리자도 비활성화
        List<Admin> admins = adminRepository.findByCompany_CompanyNum(companyNum);
        for (Admin admin : admins) {
            admin.setAdminActive("INACTIVE");
        }
        adminRepository.saveAll(admins);
    }

    @Override
    public void activateCompany(Long companyNum) {
        Company company = companyRepository.findById(companyNum)
                .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));
        company.setCompanyStatus("ACTIVE");
        companyRepository.save(company);
    }

    @Override
    public List<AdminDTO> getCompanyAdmins(Long companyNum) {

        List<Admin> adminList = adminRepository.findByCompany_CompanyNum(companyNum);
        List<AdminDTO> adminDTOList = new ArrayList<>();
        for (Admin admin : adminList) {
            AdminDTO adminDTO = modelMapper.map(admin, AdminDTO.class);
            adminDTOList.add(adminDTO);
        }

       return adminDTOList;
    }

    @Override
    public List<CompanyDTO> getCompanyList(Long companyNum) {   //특정 회사 번호에 해당하는 회사만 리스트 반환

        List<Company> companyList = companyRepository.findByCompanyNum(companyNum);
        List<CompanyDTO> companyDTOList = new ArrayList<>();
        for (Company company : companyList) {
            CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);
            companyDTOList.add(companyDTO);
        }

        return companyDTOList;
    }

    @Override
    public List<CompanyDTO> getBnFList() {
        List<CompanyDTO> list = new ArrayList<>();
        List<CompanyDTO> fcL = companyRepository.findByCompanyType("facility")
                .stream().map(data->modelMapper.map(data,CompanyDTO.class))
                .toList();
        List<CompanyDTO> brL = companyRepository.findByCompanyType("branch")
                .stream().map(data->modelMapper.map(data,CompanyDTO.class))
                .toList();
        list.addAll(brL);
        list.addAll(fcL);
        return list;
    }

    private CompanyDTO convertToCompanyDTO(Company company) {
        CompanyDTO dto = modelMapper.map(company, CompanyDTO.class);

        dto.setCompanyParentName("-");
        dto.setBranchName("-");
        dto.setFacilityName("-");

        String type = company.getCompanyType();

        if ("center".equals(type)) {
            dto.setCompanyParentName(company.getCompanyName());
        } else if ("branch".equals(type)) {
            dto.setBranchName(company.getCompanyName());
            if (company.getCompanyParent() != null) {
                companyRepository.findById(company.getCompanyParent())
                        .ifPresent(parent -> dto.setCompanyParentName(parent.getCompanyName()));
            }
        } else if ("facility".equals(type)) {
            dto.setFacilityName(company.getCompanyName());
            if (company.getCompanyParent() != null) {
                companyRepository.findById(company.getCompanyParent())
                        .ifPresent(parent -> dto.setCompanyParentName(parent.getCompanyName()));
            }
        }

        return dto;
    }

    @Override
    public Page<CompanyDTO> companyList(Pageable pageable) {    //페이징 적용된 회사 목록

        Page<Company> companyList = companyRepository.findAll(pageable);
        Page<CompanyDTO> companyDTOS = companyList.map(company -> modelMapper.map(company, CompanyDTO.class));

        return companyDTOS;
    }


}
