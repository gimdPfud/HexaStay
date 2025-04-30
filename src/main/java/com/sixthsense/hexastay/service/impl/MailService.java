package com.sixthsense.hexastay.service.impl;


import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@EnableAsync
@Service
@RequiredArgsConstructor
public class MailService {

    //라이브러리 java mailsender
    private final JavaMailSender mailSender;

    //application.properties 설정 발송 메일정보랑 동일
    //보내는 메일 주소
    private final String senderEmail = "kimbbuhhwan@gmail.com";



    //중간 테이블 Room 을 통으로 가져 오는 로직
    @Async
    public void sendRoomReservationEmail(Room room) {
        try {
            Member member = room.getMember();
            HotelRoom hotelRoom = room.getHotelRoom();

            String toEmail = member.getMemberEmail();
            String memberName = member.getMemberName();
            String hotelRoomName = hotelRoom.getHotelRoomName();
            LocalDateTime checkIn = room.getCheckInDate();
            LocalDateTime checkOut = room.getCheckOutDate();
            String roomPassword = room.getRoomPassword();
            Long hotelRoomNum = hotelRoom.getHotelRoomNum();

            // 포맷팅 예쁘게
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            //발송 메일 디자인 수정 여기서 하면 됨
            String htmlContent =
                    "<div style='max-width:600px; margin:0 auto; font-family:Arial, sans-serif; color:#333; border:1px solid #ddd; border-radius:8px; overflow:hidden; box-shadow:0 2px 10px rgba(0,0,0,0.1);'>"
                            + "<div style='background-color:#004080; color:white; padding:20px;'>"
                            + "<h2 style='margin:0;'>HexaStay 호텔 예약 정보</h2>"
                            + "</div>"
                            + "<div style='padding:30px;'>"
                            + "<p style='font-size:16px; margin-bottom:5px;'><strong>" + memberName + "</strong>님, 안녕하세요.</p>"
                            + "<p style='font-size:15px;'>아래는 고객님의 예약 상세 정보입니다.</p>"
                            + "<table style='width:100%; border-collapse:collapse; font-size:14px; margin-top:20px;'>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>호텔룸</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + hotelRoomName + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>체크인</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + checkIn.format(formatter) + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>체크아웃</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + checkOut.format(formatter) + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>호텔 입장 비밀번호</td><td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#d9534f; font-size:18px; font-weight:bold;'>" + roomPassword + "</span></td></tr>"
                            + "</table>"
                            + "<div style='margin-top:30px; text-align:center;'>"
                            + "<a href='http://localhost:8090/qr/" + hotelRoomNum + "' "
                            + "style='display:inline-block; padding:12px 24px; background-color:#004080; color:#fff; text-decoration:none; border-radius:5px; font-size:16px;'>"
                            + "📲 QR 인증 페이지 이동</a>"
                            + "</div>"
                            + "<p style='margin-top:40px; font-size:13px; color:#777;'>문의 사항이 있으시면 언제든지 연락 주시기 바랍니다.<br>감사합니다 - HexaStay 호텔 드림</p>"
                            + "</div>"
                            + "<div style='background-color:#f9f9f9; text-align:center; font-size:12px; color:#aaa; padding:15px;'>"
                            + "ⓒ 2025 HexaStay. All rights reserved."
                            + "</div>"
                            + "</div>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);  //받는 사람 메일 주소
            helper.setSubject("호텔 예약 비밀번호 안내"); //메이 제목
            helper.setFrom(senderEmail);    //보내는 사람 메일 주소
            helper.setText(htmlContent, true);  //디자인 한거 여기서 set

            mailSender.send(message);
            log.info("✅ 메일 발송 성공 - 수신자: {}", toEmail);

        } catch (Exception e) {
            log.error("❌ 메일 발송 실패: {}", e.getMessage());
        }
    }


    /**
     * 예약 메일 발송
     * @param toEmail 수신자 이메일
     * @param memberName 회원 이름
     * @param hotelRoomName 호텔룸 이름
     * @param checkIn 체크인 날짜
     * @param checkOut 체크아웃 날짜
     * @param roomPassword 룸 비밀번호
     * @param hotelRoomNum QR 링크를 위한 호텔룸 번호
     * */


    //파라미터로 일일이 봐아 오는 로직
    //mail 테스트용 메서드 - localhost:8090/review/mail
    @Async
    public void sendRoomReservationEmailpa(String toEmail,
                                         String memberName,
                                         String hotelRoomName,
                                         LocalDateTime checkIn,
                                         LocalDateTime checkOut,
                                         String roomPassword,
                                         Long hotelRoomNum) {
        log.info("📨 메일 발송 비동기 시작 - 수신자: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("호텔 예약 비밀번호 안내");
            helper.setFrom(senderEmail);

            // 포맷팅 예쁘게
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            String htmlContent =
                    "<div style='max-width:600px; margin:0 auto; font-family:Arial, sans-serif; color:#333; border:1px solid #ddd; border-radius:8px; overflow:hidden; box-shadow:0 2px 10px rgba(0,0,0,0.1);'>"
                            + "<div style='background-color:#004080; color:white; padding:20px;'>"
                            + "<h2 style='margin:0;'>HexaStay 호텔 예약 정보</h2>"
                            + "</div>"
                            + "<div style='padding:30px;'>"
                            + "<p style='font-size:16px; margin-bottom:5px;'><strong>" + memberName + "</strong>님, 안녕하세요.</p>"
                            + "<p style='font-size:15px;'>아래는 고객님의 예약 상세 정보입니다.</p>"
                            + "<table style='width:100%; border-collapse:collapse; font-size:14px; margin-top:20px;'>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>호텔룸</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + hotelRoomName + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>체크인</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + checkIn.format(formatter) + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>체크아웃</td><td style='padding:10px; border-bottom:1px solid #eee;'>" + checkOut.format(formatter) + "</td></tr>"
                            + "<tr><td style='padding:10px; border-bottom:1px solid #eee; font-weight:bold;'>호텔 입장 비밀번호</td><td style='padding:10px; border-bottom:1px solid #eee;'><span style='color:#d9534f; font-size:18px; font-weight:bold;'>" + roomPassword + "</span></td></tr>"
                            + "</table>"
                            + "<div style='margin-top:30px; text-align:center;'>"
                            + "<a href='http://localhost:8090/qr/" + hotelRoomNum + "' "
                            + "style='display:inline-block; padding:12px 24px; background-color:#004080; color:#fff; text-decoration:none; border-radius:5px; font-size:16px;'>"
                            + "📲 QR 인증 페이지 이동</a>"
                            + "</div>"
                            + "<p style='margin-top:40px; font-size:13px; color:#777;'>문의 사항이 있으시면 언제든지 연락 주시기 바랍니다.<br>감사합니다 - HexaStay 호텔 드림</p>"
                            + "</div>"
                            + "<div style='background-color:#f9f9f9; text-align:center; font-size:12px; color:#aaa; padding:15px;'>"
                            + "ⓒ 2025 HexaStay. All rights reserved."
                            + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("✅ 메일 발송 성공 - 수신자: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ 메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e.getMessage());
        }
    }




}
