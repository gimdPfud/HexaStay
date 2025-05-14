package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CommentDTO;
import com.sixthsense.hexastay.dto.CommentViewDTO;
import org.springframework.data.domain.Page;

public interface CommService {

    Page<CommentViewDTO> getComments(Integer commCategory, Long commContentNum, int page);
    void commRegister(CommentDTO commentDTO);
    public void commDelete(Long commNum);

    }

