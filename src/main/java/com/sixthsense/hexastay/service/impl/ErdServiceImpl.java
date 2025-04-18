package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.ErdDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Erd;
import com.sixthsense.hexastay.repository.ErdRepository;
import com.sixthsense.hexastay.service.ErdService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
            String fileFirstName = erdDTO.getErdSKU() + "_" + erdDTO.getErdName();
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

        if (erdDTO.getComapnyNum() != null) {
            erd.getCompany().setCompanyNum(erdDTO.getComapnyNum());
        } else if (erdDTO.getStoreNum() != null) {
            erd.getStore().setStoreNum(erdDTO.getStoreNum());
        }

        erdRepository.save(erd);
    }




}

