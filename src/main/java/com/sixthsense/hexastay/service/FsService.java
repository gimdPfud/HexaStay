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
import com.sixthsense.hexastay.entity.Facilities;
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

    //등록 반환 fsNum
    public Long fsInsert(FacilitiesDTO dto) throws IOException {
        log.info("등록시작 "+dto);
        Facilities fs = modelMapper.map(dto,Facilities.class);
        fs.setFsAmount(dto.getFsAmountMax());
        fs = fsRepository.save(fs);

        log.info("1차저장 끝 "+fs);
        //프로필 이미지 처리
        if (dto.getFsPicture() != null && !dto.getFsPicture().isEmpty()) {
            String fileOriginalName = dto.getFsPicture().getOriginalFilename();
            String fileFirstName = fs.getFacilitiesNum() + "_" + dto.getFsName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            dto.setFsPictureMeta("/company/fs/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "company/fs/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "company/fs/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            dto.getFsPicture().transferTo(uploadPath.toFile());
        }
        fs.setFsPictureMeta(dto.getFsPictureMeta());
        fs = fsRepository.save(fs);
        log.info("2차저장 끝 : "+fs);
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

    //수정 반환 companyNum
    public Long modify (FacilitiesDTO dto) throws IOException {
        Facilities entity = fsRepository.findById(dto.getFacilitiesNum()).orElseThrow(EntityNotFoundException::new);
        entity.setFsAmountMax(dto.getFsAmountMax());
        if (entity.getFsAmount() > dto.getFsAmountMax()) { entity.setFsAmount(dto.getFsAmountMax()); }
        entity.setFsName(dto.getFsName());
        entity.setFsContent(dto.getFsContent());
        entity.setFsPrice(dto.getFsPrice());
        entity.setFsStatus(dto.getFsStatus());
        
        //이미지 수정
        if(dto.getFsPicture()!=null && !dto.getFsPicture().isEmpty()) {//이미지 새로 넣었고
            if (entity.getFsPictureMeta()!=null  && !entity.getFsPictureMeta().isEmpty()) {//기존 이미지가 있다면
                Path filePath = Paths.get(System.getProperty("user.dir"), entity.getFsPictureMeta().substring(1));
                Files.deleteIfExists(filePath);//삭제
            }
            /*이미지 등록 절차...*/
            String fileOriginalName = dto.getFsPicture().getOriginalFilename();
            String fileFirstName = entity.getCompany().getCompanyNum() + "_" + dto.getFacilitiesNum() + "_" + dto.getFsName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            dto.setFsPictureMeta("/company/fs/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "company/fs/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "company/fs/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            dto.getFsPicture().transferTo(uploadPath.toFile());
            entity.setFsPictureMeta(dto.getFsPictureMeta());
        }
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

    //삭제 반환 fsNum
    public Long delete(Long fsNum){
        Facilities fs = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        fsRepository.delete(fs);
        return fs.getCompany().getCompanyNum();
    }
}
