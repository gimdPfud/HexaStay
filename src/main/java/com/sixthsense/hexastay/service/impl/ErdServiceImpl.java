package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.ErdDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Erd;
import com.sixthsense.hexastay.repository.ErdRepository;
import com.sixthsense.hexastay.service.ErdService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ToString
@Log4j2
@Service
@RequiredArgsConstructor
public class ErdServiceImpl implements ErdService {

    private final ModelMapper modelMapper = new ModelMapper();
    private final ErdRepository erdRepository;

    public void insert(ErdDTO erdDTO) throws IOException {
        if (erdDTO.getErdPicture() != null && !erdDTO.getErdPicture().isEmpty()) {
            String fileOriginalName = erdDTO.getErdPicture().getOriginalFilename();
            String fileFirstName = erdDTO.getErdSku() + "_" + erdDTO.getErdName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            erdDTO.setErdPictureMeta("/erd/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "erd/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "erd/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            erdDTO.getErdPicture().transferTo(uploadPath.toFile());

        }

        Erd erd = modelMapper.map(erdDTO, Erd.class);

        if (erdDTO.getCompanyNum() != null) {
            erd.getCompany().setCompanyNum(erdDTO.getCompanyNum());
        } else if (erdDTO.getStoreNum() != null) {
            erd.getStore().setStoreNum(erdDTO.getStoreNum());
        }

        erdRepository.save(erd);
    }

    //소속 리스트
    @Override
    public Page<ErdDTO> getErdList(AdminDTO adminDTO, Pageable pageable) {
        Page<Erd> erdList = null;
        if (adminDTO.getCompanyNum() != null) {
            erdList = erdRepository.findByCompany_CompanyNum(adminDTO.getCompanyNum(), pageable);
        } else if (adminDTO.getStoreNum() != null) {
            erdList = erdRepository.findByStore_StoreNum(adminDTO.getStoreNum(), pageable);
        }
        return erdList.map(erd -> modelMapper.map(erd, ErdDTO.class));
    }


    @Override
    public ErdDTO getErd(Long erdNum) {
        Erd erd = erdRepository.findById(erdNum).orElseThrow( () ->(new NotFoundException("품목을 찾지 못했습니다.")));
        return modelMapper.map(erd, ErdDTO.class);
    }


    @Override
    public void update(ErdDTO erdDTO) throws IOException {
        Erd existingErd = erdRepository.findById(erdDTO.getErdNum())
                .orElseThrow(() -> new NotFoundException("품목을 찾지 못했습니다."));

        // 새로운 이미지가 있는 경우
        if (erdDTO.getErdPicture() != null && !erdDTO.getErdPicture().isEmpty()) {
            String fileOriginalName = erdDTO.getErdPicture().getOriginalFilename();
            String fileFirstName = erdDTO.getErdSku() + "_" + erdDTO.getErdName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            erdDTO.setErdPictureMeta("/erd/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "erd/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "erd/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            erdDTO.getErdPicture().transferTo(uploadPath.toFile());
        } else if (existingErd.getErdPictureMeta() != null) {
            // 새로운 이미지가 없고 기존 이미지가 있는 경우
            erdDTO.setErdPictureMeta(existingErd.getErdPictureMeta());
        }

        Erd erd = modelMapper.map(erdDTO, Erd.class);

        if (erdDTO.getCompanyNum() != null) {
            erd.getCompany().setCompanyNum(erdDTO.getCompanyNum());
        } else if (erdDTO.getStoreNum() != null) {
            erd.getStore().setStoreNum(erdDTO.getStoreNum());
        }

        erdRepository.save(erd);
    }


}

