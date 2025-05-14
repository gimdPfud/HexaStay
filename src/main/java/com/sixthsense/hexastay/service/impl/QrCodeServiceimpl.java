package com.sixthsense.hexastay.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

@Service
@Log4j2
public class QrCodeServiceimpl {

    // QR 코드 생성 및 파일로 저장하는 메소드
    public void generateQrCodeToFile(String content, String filePath) throws IOException {
        try {
            // QR 코드 생성 옵션 설정
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // Error correction level (L = 7% error tolerance)
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  // 문자 인코딩 설정

            // QR 코드 생성
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);

            // 파일 경로 지정 (QR 코드 이미지 파일로 저장)
            File outputFile = new File(filePath);
            // 디렉토리 생성 (디렉토리가 없을 경우)
            outputFile.getParentFile().mkdirs();

            // QR 코드 이미지를 파일로 저장
            MatrixToImageWriter.writeToFile(bitMatrix, "PNG", outputFile);
        } catch (Exception e) {
            throw new IOException("QR 코드 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }



}
