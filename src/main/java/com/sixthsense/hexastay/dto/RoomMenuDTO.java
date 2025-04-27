/***********************************************
 * 클래스명 : RoomMenuDTO
 * 기능 : 룸 메뉴 정보를 담는 DTO 클래스
 * - 룸 메뉴의 고유 번호, 이름, 가격, 수량, 설명, 카테고리, 판매 상태 등의 정보를 관리
 * - 좋아요 및 싫어요 수, 이미지 파일 및 관련 메타데이터 포함
 * - 등록일자와 수정일자 정보 관리
 * - 호텔 방 번호를 참조하여 룸과의 관계 설정
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 (createDate, modifyDate), 김예령
 * 수정 : 2025-04-17 클래스 주석 추가, 자동 생성
 * ***********************************************/

package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.Room;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMenuDTO {

    private Long roomMenuNum;

    private String roomMenuName;                 //상품이름

    private Integer roomMenuPrice;                //상품가격

    private Integer roomMenuAmount;                 // 상품수량

    private String roomMenuContent;              //상품설명

    private String roomMenuCategory;             //카테고리

    private String roomMenuStatus;              //품절여부

    private Integer roomMenuLikes = 0; // 좋아요

    private Integer roomMenuDisLikes = 0; // 싫어요

    private MultipartFile roomMenuImage; // 룸 메뉴 이미지

    private String roomMenuImageMeta; //룸 메뉴 사진 정보

    private String originalImageMeta; // 기존 이미지의 경로

    private LocalDateTime createDate;    //등록일자

    private LocalDateTime modifyDate;    //수정일자

    private Long hotelRoomNum;         //방 참조

    private Room room; // 룸을 참조

    private Boolean supportsMultilang = false;  // 다국어 체크 여부

    private Boolean approvedByDev = false; // 개발자 승인 여부

    private List<RoomMenuOptionDTO> options = new ArrayList<>();


    public Integer getRoomMenuAmount() {
        if (this.roomMenuAmount == null) {
            return 0; // 기본값으로 0을 반환
        }
        return this.roomMenuAmount;
    }

}