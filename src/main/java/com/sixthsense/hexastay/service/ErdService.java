package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.ErdDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ErdService {

    void insert(ErdDTO erdDTO) throws IOException;

    Page<ErdDTO> getErdList(AdminDTO adminDTO, Pageable pageable);
    ErdDTO getErd(Long erdNum);
    void update(ErdDTO erdDTO) throws IOException;
}
