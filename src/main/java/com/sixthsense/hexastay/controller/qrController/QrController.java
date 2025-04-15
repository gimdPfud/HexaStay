package com.sixthsense.hexastay.controller.qrController;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class QrController {

    @GetMapping("/")
    public String index() {

        return "qr/index"; //시작 페이지
    }

    //요청 - > qr을 작성을 -> 응답
    //쓰기 실패와 입출력 실패시 예외 처리
    //reponse는 ajax를 통한 비동기식: 결과 값을
    @GetMapping("/qr")
    public ResponseEntity<byte[]> qrMake() throws WriterException, IOException {
        //QR를 만들기 위한 정보
        int width = 200;
        int height = 200;
        String url = "http://google.com";   //http://사용자 페이지? 예약코드 = 3242343 응용

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

            //성공메세지와 png이미지 파일을 저장
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());

        } catch (RuntimeException e) {
            System.out.println("QR생성중 오류 발생");
        }
        return null;


    }


}
