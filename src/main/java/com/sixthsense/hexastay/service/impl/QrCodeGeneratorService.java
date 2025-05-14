package com.sixthsense.hexastay.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//QR 생성용 별도 로직
@Log4j2
@Service
public class QrCodeGeneratorService {

    /**
     * QR 코드 이미지를 생성하고 저장합니다.
     *
     * @param qrText       인코딩할 QR 문자열 (서비스 단에서 조립된 URL)
     * @param fileBaseName 저장할 파일 이름 앞부분 (예: Room101)

     * @return 생성된 파일 이름 (예: Room101_qr.png)
     */
    // QR 코드를 만들어서 컴퓨터에 저장해주는 함수예요.
// qrText: QR 코드 안에 넣을 글자 (예: 웹 주소)
// fileBaseName: 저장할 이미지 이름의 앞부분 (예: 방이름)
    public String generateQrCode(String qrText, String fileBaseName) {
        try {
            // 1. QR 코드 안에 들어갈 글자 확인해보기
            log.info("인코딩할 qrText 값: {}", qrText);

            // 2. 이미지 파일 이름 만들기 (예: Room101_qr.png)
            String fileName = fileBaseName + "_qr.png";

            // 3. 이미지가 저장될 폴더 만들기 (폴더 이름: qrfile)
            Path createPath = Paths.get("c:/data/hexastay", "qrfile");

            // 4. 저장될 파일의 전체 경로 만들기
            Path uploadPath = createPath.resolve(fileName);

            // 5. 폴더가 아직 없으면 새로 만들기
            if (!Files.exists(createPath)) {
                Files.createDirectories(createPath);
            }

            // 6. QR 코드를 만들기 위한 설정
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 한글도 잘 되게 설정
            hints.put(EncodeHintType.MARGIN, 4);              // 테두리를 4칸 정도 남기기 - 기본 4 작을수로 마진이 줄어듬

            // 7. QR 코드를 600x600 크기로 만들기
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 600, 600, hints);

            // 8. 색상 설정 - 검정색 네모 + 흰색 배경 - QR 이미지 배경 색상 설정
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);

            // 9. QR 코드를 이미지(PNG)로 컴퓨터에 저장하기
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", uploadPath, config);

            // 10. 나중에 화면에서 보여줄 수 있게 파일 경로를 리턴해줘요
            return "/qrfile/" + fileName;

        } catch (Exception e) {
            // 문제가 생기면 알려줘요
            throw new RuntimeException("QR 코드 생성 실패: " + e.getMessage(), e);
        }
    }

}

