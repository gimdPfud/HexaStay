package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.SettleService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@ToString
@Log4j2
@Service
@RequiredArgsConstructor
public class SettleServiceImpl implements SettleService {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    // 컴퍼니넘 소속 호텔 찾기
    @Override
    public List<CompanyDTO> getCompanyParent(Long companyNum) {
        List<Company> companyList = companyRepository.findByCompanyParent(companyNum);
        List<CompanyDTO> companyDTOList = new ArrayList<>();
        for (Company company : companyList) {
            CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);
            companyDTOList.add(companyDTO);
        }
        return companyDTOList;
    }


}
