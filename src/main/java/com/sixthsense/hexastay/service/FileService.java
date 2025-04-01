package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.UploadFileDTO;
import com.sixthsense.hexastay.entity.UploadFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    public List<UploadFile> saveFile(UploadFile uploadFile, MultipartFile[] files) throws IOException;
    public List<UploadFileDTO> getFileList(Integer category, Long fileContentNum);

    public ResponseEntity<Void> DeleteFileList(List<Long> files) throws IOException;

}
