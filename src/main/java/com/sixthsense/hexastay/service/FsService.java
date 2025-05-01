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
import com.sixthsense.hexastay.dto.FsViewDTO;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FsService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final FacilitiesRepository fsRepository;
    private final CompanyRepository companyRepository;

    //등록 반환 fsNum
    public Long fsInsert(FacilitiesDTO dto) throws IOException {
        log.info("등록시작 "+dto);
        Facilities fs = modelMapper.map(dto,Facilities.class);
        fs.setFsAmount(0);
        fs = fsRepository.save(fs);
        log.info("1차저장 끝 "+fs);
        return fs.getCompany().getCompanyNum();
    }

    //목록 반환 DTO
    public List<FacilitiesDTO> list (Long companyNum){
        List<Facilities> facilities = fsRepository.findByCompany_CompanyNum(companyNum);
        return facilities.stream().map((element) ->
                        modelMapper.map(element, FacilitiesDTO.class)
                                .setCompanyDTO(modelMapper.map(element.getCompany(), CompanyDTO.class)))
                .toList();
    }

    //읽기 반환 DTO
    public FacilitiesDTO read (Long fsNum){
        Facilities a = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(a,FacilitiesDTO.class).setCompanyDTO(modelMapper.map(a.getCompany(),CompanyDTO.class));
    }
    public FsViewDTO readMobile (Long num){
        // 시설(company)찾음
        Company facility = companyRepository.findById(num).orElseThrow(EntityNotFoundException::new);
        CompanyDTO cdto = modelMapper.map(facility,CompanyDTO.class);

        // 반환할거에 시설(company)정보 세팅
        FsViewDTO result = new FsViewDTO(cdto);

        // 반환할거에 시설(fs) 찾아서 넣어야됨.
        List<Facilities> fsslist = fsRepository.findByCompany_CompanyNum(num);
        fsslist.forEach(fss->{
            FacilitiesDTO fssDTO = modelMapper.map(fss,FacilitiesDTO.class);
            fssDTO.setCompanyDTO(cdto);
            result.addFssList(fssDTO);
        });

//        log.info(result.toString());

        return result;
    }

    //수정 반환 companyNum
    public Long modify (FacilitiesDTO dto) throws IOException {
        Facilities entity = fsRepository.findById(dto.getFacilitiesNum()).orElseThrow(EntityNotFoundException::new);
        entity.setFsAmountMax(dto.getFsAmountMax());
        entity.setFsName(dto.getFsName());
        entity.setFsContent(dto.getFsContent());
        entity.setFsPrice(dto.getFsPrice());
        entity.setFsStatus(dto.getFsStatus());
        return entity.getCompany().getCompanyNum();
    }

    //상태변경 반환 fsNum
    public Long fsYes(Long fsNum){
        fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new).setFsStatus("yes");
        return fsNum;
    }
    public Long fsNo(Long fsNum){
        fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new).setFsStatus("no");
        return fsNum;
    }
    //수량변경
    public Long refill(Long fsNum){
        Facilities a = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        a.setFsAmount(0);
        return a.getCompany().getCompanyNum();
    }

    //삭제 반환 fsNum
    public Long delete(Long fsNum){
        Facilities fs = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        fsRepository.delete(fs);
        return fs.getCompany().getCompanyNum();
    }
}