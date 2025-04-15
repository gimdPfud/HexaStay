package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderItemDTO;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2


public class RoomMenuOrderServiceImpl implements RoomMenuOrderService {

    private final RoomMenuOrderRepository roomMenuOrderRepository;
    private final MemberRepository memberRepository;
    private final RoomMenuRepository roomMenuRepository;
    private final RoomMenuCartItemRepository roomMenuCartItemRepository;
    private final RoomMenuCartRepository roomMenuCartRepository;

    /***************************************************
     *
     * 클래스명   : roomMenuOrderInsert
     * 기능      : 룸서비스 메뉴 주문을 처리하는 서비스 메소드
     *            - 전달받은 DTO를 통해 주문할 메뉴 정보와 수량을 확인
     *            - 이메일을 기반으로 회원 정보를 조회하고, 해당 회원의 주문을 생성
     *            - 주문 수량이 재고보다 많을 경우 예외 처리
     *            - 주문 정보 및 주문 아이템 정보를 저장하고 주문 번호를 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-11
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public Long roomMenuOrderInsert(RoomMenuOrderDTO roomMenuOrderDTO, String email) {
        log.info("룸 메뉴 오더 서비스 주문처리 진입" + email);
        // 주문할 룸메뉴 ID를 기반으로 메뉴 정보를 조회
        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuOrderDTO.getRoomMenuOrderNum())
                .orElseThrow(EntityNotFoundException::new);  // 메뉴가 없으면 예외 발생

        // 이메일을 기반으로 회원 정보 조회
        Member member = memberRepository.findByMemberEmail(email);

        // 새로운 주문 객체 생성
        RoomMenuOrder roomMenuOrder = new RoomMenuOrder();

        // 주문한 회원 정보 설정
        roomMenuOrder.setMember(member);

        // 주문 상태 설정 (기본값: ORDER)
        roomMenuOrder.setRoomMenuOrderStatus(RoomMenuOrderStatus.ORDER);

        // 주문 아이템들을 담을 리스트 생성
        List<RoomMenuOrderItem> roomMenuOrderItemList = new ArrayList<>();

        // 주문 아이템 생성 및 정보 설정
        RoomMenuOrderItem roomMenuOrderItem = new RoomMenuOrderItem();
        roomMenuOrderItem.setRoomMenu(roomMenu);  // 주문할 메뉴 설정
        roomMenuOrderItem.setRoomMenuOrderAmount(roomMenuOrderDTO.getRoomMenuOrderAmount());  // 주문 수량 설정
        roomMenuOrderItem.setRoomMenuOrderPrice(roomMenu.getRoomMenuPrice());  // 메뉴 가격 설정
        roomMenuOrderItem.setRoomMenuOrder(roomMenuOrder);  // 주문 객체와 연관관계 설정

        // 리스트에 아이템 추가
        roomMenuOrderItemList.add(roomMenuOrderItem);

        // 현재 재고보다 주문 수량이 많으면 예외 발생
        if (roomMenu.getRoomMenuAmount() - roomMenuOrderDTO.getRoomMenuOrderAmount() < 0) {
            throw new IllegalArgumentException("요청 수량이 현재 재고보다 많습니다. (현재수량 : " + roomMenu.getRoomMenuAmount() + ")");
        }

        // 주문 수량만큼 재고 감소
        roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - roomMenuOrderDTO.getRoomMenuOrderAmount());

        // 주문 객체에 아이템 리스트 설정
        roomMenuOrder.setOrderItems(roomMenuOrderItemList);

        // 주문 정보 저장
        RoomMenuOrder roomMenuOrderA = roomMenuOrderRepository.save(roomMenuOrder);

        // 저장된 주문의 주문 번호 반환
        return roomMenuOrderA.getRoomMenuOrderNum();
    }

    @Override
    public Long roomMenuOrderInsertFromCart(String email) {
        log.info("장바구니 기반 주문 생성 시작 - email: {}", email);

        // 1. 로그인한 회원 조회
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) throw new IllegalArgumentException("회원 정보가 존재하지 않습니다.");

        // 2. 해당 회원의 장바구니 가져오기
        RoomMenuCart cart = roomMenuCartRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 존재하지 않습니다."));

        // 3. 장바구니 아이템들 가져오기
        List<RoomMenuCartItem> cartItems = roomMenuCartItemRepository.findByRoomMenuCart(cart);
        if (cartItems.isEmpty()) throw new IllegalStateException("장바구니가 비어 있습니다.");

        // 4. 주문 객체 생성
        RoomMenuOrder roomMenuOrder = new RoomMenuOrder();
        roomMenuOrder.setMember(member);
        roomMenuOrder.setRoomMenuOrderStatus(RoomMenuOrderStatus.ORDER);

        List<RoomMenuOrderItem> orderItems = new ArrayList<>();

        // 5. 장바구니 아이템 → 주문 아이템으로 변환
        for (RoomMenuCartItem cartItem : cartItems) {
            RoomMenu roomMenu = cartItem.getRoomMenu();

            // 재고 확인
            if (roomMenu.getRoomMenuAmount() < cartItem.getRoomMenuCartItemAmount()) {
                throw new IllegalStateException("재고가 부족한 메뉴가 존재합니다: " + roomMenu.getRoomMenuName());
            }

            // 주문 아이템 생성
            RoomMenuOrderItem orderItem = new RoomMenuOrderItem();
            orderItem.setRoomMenu(roomMenu);
            orderItem.setRoomMenuOrderAmount(cartItem.getRoomMenuCartItemAmount());
            orderItem.setRoomMenuOrderPrice(roomMenu.getRoomMenuPrice());
            orderItem.setRoomMenuOrder(roomMenuOrder);

            // 재고 차감
            roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - cartItem.getRoomMenuCartItemAmount());

            orderItems.add(orderItem);
        }

        // 6. 주문과 아이템 연결
        roomMenuOrder.setOrderItems(orderItems);

        // 7. 주문 저장
        RoomMenuOrder savedOrder = roomMenuOrderRepository.save(roomMenuOrder);

        // 8. 장바구니 비우기
        roomMenuCartItemRepository.deleteAll(cartItems);

        return savedOrder.getRoomMenuOrderNum();
    }

    @Override
    public List<RoomMenuOrderDTO> getOrderListByEmail(String email) {
        Member member = memberRepository.findByMemberEmail(email);
        List<RoomMenuOrder> orders = roomMenuOrderRepository.findByMemberOrderByRegDateDesc(member);

        return orders.stream().map(order -> {
            RoomMenuOrderDTO dto = new RoomMenuOrderDTO();
            dto.setRoomMenuOrderNum(order.getRoomMenuOrderNum());
            dto.setRoomMenuOrderStatus(order.getRoomMenuOrderStatus());
            dto.setRegDate(order.getRegDate());

            List<RoomMenuOrderItemDTO> itemDTOList = order.getOrderItems().stream().map(item -> {
                RoomMenuOrderItemDTO itemDTO = new RoomMenuOrderItemDTO();
                itemDTO.setRoomMenuOrderItemNum(item.getRoomMenuOrderItemNum().toString());
                itemDTO.setRoomMenuOrderItemAmount(item.getRoomMenuOrderAmount());
                itemDTO.setRoomMenuOrderItemPrice(item.getRoomMenuOrderPrice());
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setOrderItemList(itemDTOList);
            return dto;
        }).collect(Collectors.toList());
    }
}