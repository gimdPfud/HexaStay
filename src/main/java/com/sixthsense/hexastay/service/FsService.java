/***********************************************
 * 클래스명 : FsService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.FacilitiesDTO;
import com.sixthsense.hexastay.dto.FacViewDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Facilities;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.FacilitiesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FsService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final FacilitiesRepository fsRepository;
    private final CompanyRepository companyRepository;

    //등록 반환 fsNum
    public Long fsInsert(FacilitiesDTO dto) {
//        log.info("등록시작 "+dto);
        Facilities fs = modelMapper.map(dto,Facilities.class);
        fs = fsRepository.save(fs);
        return fs.getCompany().getCompanyNum();
    }

    //목록 반환 DTO
//    public List<FacilitiesDTO> list (Long companyNum){
//        List<Facilities> facilities = fsRepository.findByCompany_CompanyNum(companyNum);
//        return facilities.stream().map((element) ->
//                        modelMapper.map(element, FacilitiesDTO.class)
//                                .setCompanyDTO(modelMapper.map(element.getCompany(), CompanyDTO.class)))
//                .toList();
//    }

    //읽기 반환 DTO
    public FacilitiesDTO read (Long companyNum){
        Facilities a = fsRepository.findByCompany_CompanyNum(companyNum);
        if(a!=null){
            return modelMapper.map(a,FacilitiesDTO.class).setCompanyDTO(modelMapper.map(a.getCompany(),CompanyDTO.class));
        }else{
            return null;
        }
    }
    public FacViewDTO readMobile (Long num){
        // 시설(company)찾음
        Company facility = companyRepository.findById(num).orElseThrow(EntityNotFoundException::new);
        CompanyDTO cdto = modelMapper.map(facility,CompanyDTO.class);

        // 반환할거에 시설(company)정보 세팅
        FacViewDTO result = new FacViewDTO(cdto);

        // 반환할거에 시설(fs) 찾아서 넣어야됨.
        Facilities fss = fsRepository.findByCompany_CompanyNum(num);
        if(fss!=null){
            FacilitiesDTO fssDTO = modelMapper.map(fss,FacilitiesDTO.class);
            fssDTO.setCompanyDTO(cdto);
            result.addFssList(fssDTO);
        }
        log.info(result.toString());
        return result;
    }

    //수정 반환 companyNum
    public Long modify (FacilitiesDTO dto) throws IOException {
        Facilities entity = fsRepository.findById(dto.getFacilitiesNum()).orElseThrow(EntityNotFoundException::new);
        entity.setFacTitle(dto.getFacTitle());
        entity.setFacContent(dto.getFacContent());
        return entity.getCompany().getCompanyNum();
    }

    //삭제 반환 fsNum
    public Long delete(Long fsNum){
        Facilities fs = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        fsRepository.delete(fs);
        return fs.getCompany().getCompanyNum();
    }
}