package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findByFileCategoryAndFileContentNum(Integer category, Long fileContentNum);
}
