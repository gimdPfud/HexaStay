package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***********************************************
 * 클래스명 : RoomMenuOrderDTO
 * 기능 : 룸 메뉴 주문 정보를 담는 DTO 클래스
 * - 주문 고유 번호, 총 주문 수량, 주문한 사용자 정보(Member) 포함
 * - 주문 상태(RoomMenuOrderStatus) 및 주문 등록 날짜 관리
 * - 주문 아이템 목록 (RoomMenuOrderItemDTO)을 리스트 형태로 포함하여 주문 상세 정보 관리
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-08
 * 수정일 : -
 * ***********************************************/

@Getter
@Setter
@ToString

public class RoomMenuOrderDTO {

    private  Long roomMenuOrderNum; // pk

    private  int roomMenuOrderAmount; // 주문의 수량

    private int totalPrice; // 주문의 총 합계 < -- AdminOrderList 에서 사용중

    private Member member;

    private RoomMenuOrderStatus roomMenuOrderStatus;

    private LocalDateTime regDate;

    private Integer discountedPrice; // 할인된 최종 금액

    private Integer originalTotalPrice; // 쿠폰 사용 전 가격

    private Long usedCouponNum; // 사용된 쿠폰의 넘버

    private String roomMenuImageMeta; //룸 메뉴 사진 정보

    private String originalImageMeta; // 기존 이미지의 경로

    private Long hotelRoom; // 호텔룸을 참조

    private String hotelRoomName; // 호텔의 이름.

    private List<RoomMenuOrderItemDTO> orderItemList = new ArrayList<>();




}
