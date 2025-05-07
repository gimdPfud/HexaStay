package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderItemDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomMenuOrderService {

    // 주문 입력
    public Long roomMenuOrderInsert(RoomMenuOrderDTO roomMenuOrderDTO, String email);

    // 주문 입력 2
    public RoomMenuOrder roomMenuOrderInsertFromCart(String email, String requestMessage,
                                                     Long couponNum, Integer discountedTotalPrice,
                                                     Pageable pageable, String password);

    // 리스트
    public Page<RoomMenuOrderDTO> getOrderListByEmail(String email, Pageable pageable);

    // 주문취소
    public void cancelRoomMenuOrder(Long orderNum, String email);

    // 판매자가 주문 정보를 확인
    public Page<RoomMenuOrderDTO> getAllOrdersForAdmin(Pageable pageable);

    // 관리자에게 알람을 띄우기
    public void RoomMenuSendOrderAlert(RoomMenuOrderDTO orderDto, RoomMenuOrder order, Pageable pageable);

    // 오더를 받았을 때의 알람 추가
    public RoomMenuOrder acceptOrder(Long orderId);

    // 주문을 완료하였을 때의 알람 추가
    public RoomMenuOrder completeOrder(Long orderId);

    // 주문을 취소하였을 때의 알람 추가.
    public RoomMenuOrder cancelOrderAsAdmin(Long orderId);

    // 공통 옵션 재고 차감

   public void deductOptionStock(RoomMenu roomMenu, RoomMenuOrderItemDTO orderItemDTO, int orderedItemQuantity);

    }

