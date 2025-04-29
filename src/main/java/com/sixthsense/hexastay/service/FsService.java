/***********************************************
 * 클래스명 : FsService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.service;

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

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FsService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final FacilitiesRepository fsRepository;

    //등록 반환 fsNum
    public Long fsInsert(FacilitiesDTO dto) throws IOException {

        Facilities fs = modelMapper.map(dto,Facilities.class);
        fs = fsRepository.save(fs);

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

        return fs.getCompany().getCompanyNum();
    }
    //읽기 반환 DTO
    
    //수정 반환 fsNum
    //삭제 반환 fsNum
    public Long delete(Long fsNum){
        Facilities fs = fsRepository.findById(fsNum).orElseThrow(EntityNotFoundException::new);
        fsRepository.delete(fs);
        return fs.getCompany().getCompanyNum();
    }
}
