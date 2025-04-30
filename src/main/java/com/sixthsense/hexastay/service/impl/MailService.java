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

    private final JavaMailSender mailSender;

    private final String senderEmail = "kimbbuhhwan@gmail.com";

    /**
     * 예약 메일 발송
     * @param toEmail 수신자 이메일
     * @param memberName 회원 이름
     * @param hotelRoomName 호텔룸 이름
     * @param checkIn 체크인 날짜
     * @param checkOut 체크아웃 날짜
     * @param roomPassword 룸 비밀번호
     * @param hotelRoomNum QR 링크를 위한 호텔룸 번호
     */
    //파라미터로 일일이 봐아 오는 로직
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

            String htmlContent = "<div style='padding:20px; font-family:Arial;'>"
                    + "<h2>호텔 체크인 정보 안내</h2>"
                    + "<p><strong>" + memberName + "</strong>님, 안녕하세요.</p>"
                    + "<p>아래의 정보로 호텔에 체크인하실 수 있습니다.</p>"
                    + "<ul>"
                    + "<li><strong>호텔룸:</strong> " + hotelRoomName + "</li>"
                    + "<li><strong>체크인:</strong> " + checkIn + "</li>"
                    + "<li><strong>체크아웃:</strong> " + checkOut + "</li>"
                    + "<li><strong>입장 비밀번호:</strong> "
                    + "<span style='color:#4CAF50; font-size:24px; font-weight:bold;'>" + roomPassword + "</span></li>"
                    + "</ul>"
                    + "<p style='margin-top:20px;'>아래 링크를 통해 QR 페이지로 이동할 수 있습니다.</p>"
                    + "<a href='http://localhost:8090/qr/" + hotelRoomNum + "' "
                    + "style='display:inline-block; background-color:#007bff; color:white; padding:10px 15px; text-decoration:none; border-radius:5px;'>"
                    + "📱 QR 인증 페이지 이동</a>"
                    + "<p style='margin-top:30px;'>즐거운 숙박 되세요!</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("✅ 메일 발송 성공 - 수신자: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ 메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e.getMessage());
        }
    }

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

            String htmlContent = "<div style='padding:20px; font-family:Arial;'>"
                    + "<h2>호텔 체크인 정보 안내</h2>"
                    + "<p><strong>" + memberName + "</strong>님, 안녕하세요.</p>"
                    + "<ul>"
                    + "<li><strong>호텔룸:</strong> " + hotelRoomName + "</li>"
                    + "<li><strong>체크인:</strong> " + checkIn.format(formatter) + "</li>"
                    + "<li><strong>체크아웃:</strong> " + checkOut.format(formatter) + "</li>"
                    + "<li><strong>호텔 입장 비밀번호:</strong> "
                    + "<span style='color:#4CAF50; font-size:24px; font-weight:bold;'>" + roomPassword + "</span></li>"
                    + "</ul>"
                    + "<a href='http://localhost:8090/qr/" + hotelRoomNum + "' "
                    + "style='display:inline-block; background-color:#007bff; color:white; padding:10px 15px; text-decoration:none; border-radius:5px;'>"
                    + "📱 QR 인증 페이지 이동</a>"
                    + "<p style='margin-top:30px;'>즐거운 숙박 되세요!</p>"
                    + "</div>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("호텔 예약 비밀번호 안내");
            helper.setFrom(senderEmail);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ 메일 발송 성공 - 수신자: {}", toEmail);

        } catch (Exception e) {
            log.error("❌ 메일 발송 실패: {}", e.getMessage());
        }
    }


}
