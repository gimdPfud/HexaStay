package com.sixthsense.hexastay.controller.qrController;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class QrController2 {

    @GetMapping("qrform")
    public String qrform() {
        return "qr/qrform";

    }

    @GetMapping("/qr2")
    public String qrMake(Model model) throws WriterException {

        //QR를 만들기 위한 정보
        int width = 200;
        int height = 200;
        String url = "http://naver.com";   //http://사용자 페이지? 예약코드 = 3242343 응용

        //QR 정보를 이용해서 QR생성
        //encode(QR내용, 포맷형식, 가로크기, 세로크기)
        BitMatrix encode = new MultiFormatWriter()
                .encode(url, BarcodeFormat.QR_CODE, width, height);

        //QR을 이미지파일전환
        //writerToStream(bitmatrix값, 이미지형식, 저장방향)
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(); //출력변수

            //bitmatrix의 데이터를 png형식으로 out에 쓰기
            MatrixToImageWriter.writeToStream(encode,"PNG", out);

            //이미지 파일로 변환
            String base64Image = "data:image/png;base64," + java.util.Base64.getEncoder().
                    encodeToString(out.toByteArray());
            model.addAttribute("qrImage", base64Image);

        } catch (RuntimeException e) {
            System.out.println("QR생성중 오류 발생");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "qr/qrpage";


    }
}
