package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.dto.CompanyDTO;

import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.AdminRepository;

import com.sixthsense.hexastay.repository.CompanyRepository;

import com.sixthsense.hexastay.service.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public void centerInsert(CompanyDTO companyDTO) throws IOException {
        log.info("center Insert Service 진입");

        if (companyDTO.getCompanyPicture() != null && !companyDTO.getCompanyPicture().isEmpty()) {
            String fileOriginalName = companyDTO.getCompanyPicture().getOriginalFilename();
            String fileFirstName = companyDTO.getCompanyNum() + "_" + companyDTO.getCompanyName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            companyDTO.setCompanyPictureMeta("/center/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "center/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "center/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            companyDTO.getCompanyPicture().transferTo(uploadPath.toFile());

        }


        //center 등록 (DTO를 Entity로 변환해서 Entity에 담고)
        Company company = modelMapper.map(companyDTO, Company.class);
        //Entity에 저장
        company = companyRepository.save(company);

        log.info("companyDTO를 Entity로 변환 완료 : " + company);
    }




}
