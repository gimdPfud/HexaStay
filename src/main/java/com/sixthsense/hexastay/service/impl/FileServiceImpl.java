package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.UploadFileDTO;
import com.sixthsense.hexastay.entity.UploadFile;
import com.sixthsense.hexastay.repository.FileRepository;
import com.sixthsense.hexastay.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@Log4j2
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public List<UploadFile> saveFile(UploadFile uploadFile, MultipartFile[] files) throws IOException {

        String filePath = "c:/data/hexastay" + "/file/";

        List<UploadFile> savedFiles = new ArrayList<>();

        for (MultipartFile file : files) {

            UploadFile uploadFiles = new UploadFile();
            String fileName = file.getOriginalFilename();
            String fileExtension = (fileName != null && fileName.contains("."))
                    ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
                    : "";

            String subDirectory = getSubDirectory(fileExtension);
            Path uploadPath = Paths.get(filePath, subDirectory);

            String webDir = "file/";
            Path webUploadPath = Paths.get(webDir, subDirectory);

            if (!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath);
            }

            String uuidFileName = UUID.randomUUID() + "-" + fileName;
            file.transferTo(uploadPath.resolve(uuidFileName).toFile());

            uploadFiles.setFileName(uuidFileName);
            uploadFiles.setFilePath(webUploadPath.toString());
            uploadFiles.setUploadedAt(LocalDateTime.now());
            uploadFiles.setFileSize(formatFileSize(file.getSize()));
            uploadFiles.setFileCategory(uploadFile.getFileCategory());
            uploadFiles.setFileContentNum(uploadFile.getFileContentNum());
            savedFiles.add(uploadFiles);

            fileRepository.save(uploadFiles);
        }


        return savedFiles;
    }


    private String getSubDirectory(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "heif", "img", "pcx", "tga", "dds", "pgf", "svg",
                 "heic" -> "images";
            case "mp4", "avi", "mov", "wmv", "flv", "webm", "mkv", "f4v", "mpg", "mpeg", "m4v" -> "videos";
            case "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "xltm", "hwp", "odt", "ods", "odp", "odb",
                 "oxt", "odf" -> "documents";
            case "zip", "rar", "tar", "7z", "egg", "jar" -> "compressed";
            case "mp3", "wav", "flac", "aac", "ogg", "wma", "mka" -> "audio";
            default -> "etc";
        };
    }

    private String formatFileSize(long size) {
        double sizeInMB = (double) size / (1024 * 1024);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(sizeInMB) + " MB";
    }


    public List<UploadFileDTO> getFileList(Integer category, Long fileContentNum) {
        List<UploadFile> files = fileRepository.findByFileCategoryAndFileContentNum(category, fileContentNum);

        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        List<UploadFileDTO> filesDTO = new ArrayList<>();

        for (UploadFile file : files) {
            UploadFileDTO fileDTO = new UploadFileDTO();
            fileDTO.setFileNum(file.getFileNum());
            fileDTO.setFilePath(file.getFilePath());
            fileDTO.setFileName(file.getFileName());
            fileDTO.setFileSize(file.getFileSize());
            fileDTO.setFileCategory(file.getFileCategory());
            fileDTO.setFileContentNum(file.getFileContentNum());
            filesDTO.add(fileDTO);
        }
        return filesDTO;
    }


    public ResponseEntity<Void> DeleteFileList(List<Long> files) throws IOException {
        List<UploadFile> deletefiles = fileRepository.findAllById(files);
        for (UploadFile file : deletefiles) {
            Path filePath = Paths.get("c:/data/hexastay", file.getFilePath(), file.getFileName());
            Files.deleteIfExists(filePath);
        }
        fileRepository.deleteAll(deletefiles);

        return ResponseEntity.ok().build();
    }

}
