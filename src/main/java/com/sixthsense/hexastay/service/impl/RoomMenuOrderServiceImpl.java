package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuOrderAlertDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final CouponRepository couponRepository;


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


    /***********************************************
     * 메서드명 : roomMenuOrderInsertFromCart
     * 기능 : 장바구니에 담긴 상품들을 기반으로 새로운 룸 메뉴 주문을 생성한다.
     * - 로그인한 회원을 찾고, 해당 회원의 장바구니와 아이템들을 조회한다.
     * - 장바구니 아이템들을 주문 아이템으로 변환하면서 재고를 확인하고 차감한다.
     * - 주문 객체를 생성하고, 주문 아이템들과 연결하여 저장한다.
     * - 주문 완료 후 장바구니를 비운다.
     * 매개변수 : String email - 주문하는 회원의 이메일
     * String requestMessage - 주문 시 요청사항
     * 반환값 : Long - 생성된 주문 번호
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-16
     * 수정일 : -
     * ***********************************************/

    @Override
    public RoomMenuOrder roomMenuOrderInsertFromCart(String email, String requestMessage, Long couponNum, Integer discountedTotalPrice) {
        log.info("장바구니 기반 주문 생성 시작 - email: {}", email);

        // 1. 로그인한 회원 조회
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) throw new IllegalArgumentException("회원 정보가 존재하지 않습니다.");

        // 2. 해당 회원의 장바구니 가져오기
        RoomMenuCart cart = roomMenuCartRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 존재하지 않습니다."));

        // 3. 장바구니 아이템들 가져오기
        List<RoomMenuCartItem> cartItems = roomMenuCartItemRepository.findByRoomMenuCart(cart);
        if (cartItems.isEmpty()) throw new IllegalStateException("장바구니에 아이템이 존재하지 않습니다..");

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
            roomMenuOrder.setMember(member);
            roomMenuOrder.setRoomMenuOrderStatus(RoomMenuOrderStatus.ORDER);
            roomMenuOrder.setRegDate(LocalDateTime.now());
            orderItem.setRoomMenu(roomMenu);
            orderItem.setRoomMenuOrderAmount(cartItem.getRoomMenuCartItemAmount());
            orderItem.setRoomMenuOrderPrice(roomMenu.getRoomMenuPrice());
            orderItem.setRoomMenuOrder(roomMenuOrder);
            orderItem.setRoomMenuOrderRequestMessage(requestMessage);
            log.info("요청사항: {}", requestMessage);

            if (discountedTotalPrice != null) {
                roomMenuOrder.setDiscountedPrice(discountedTotalPrice); // 새로운 필드 필요
            }

            // 쿠폰 사용 처리
            if (couponNum != null) {
                Coupon coupon = couponRepository.findById(couponNum)
                        .orElseThrow(() -> new EntityNotFoundException("쿠폰이 존재하지 않습니다."));
                coupon.setUsed(true); // 사용 처리
                couponRepository.save(coupon); // 변경 사항 저장
            }

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

        return roomMenuOrder;
    }

    /***********************************************
     * 메서드명 : getOrderListByEmail
     * 기능 : 특정 이메일을 가진 회원의 주문 목록을 조회하여 DTO 리스트로 반환한다.
     * - 회원 정보를 조회하고, 해당 회원의 주문 목록을 등록일자 내림차순으로 가져온다.
     * - 각 주문 정보를 RoomMenuOrderDTO로 변환하고, 주문 아이템 정보도 RoomMenuOrderItemDTO로 변환하여 포함한다.
     * 매개변수 : String email - 조회할 회원의 이메일
     * 반환값 : List<RoomMenuOrderDTO> - 주문 정보 DTO 리스트
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-16
     * 수정일 : -
     * ***********************************************/

    @Override
    public Page<RoomMenuOrderDTO> getOrderListByEmail(String email, Pageable pageable) {
        log.info("주문 리스트 서비스 진입 : " + email);
        Member member = memberRepository.findByMemberEmail(email);
        Page<RoomMenuOrder> orderPage = roomMenuOrderRepository.findByMemberOrderByRegDateDesc(member, pageable);

        // Page의 map 메서드를 활용하여 DTO로 변환
        Page<RoomMenuOrderDTO> dtoPage = orderPage.map(order -> {
            RoomMenuOrderDTO dto = new RoomMenuOrderDTO();
            dto.setRoomMenuOrderNum(order.getRoomMenuOrderNum());
            dto.setRoomMenuOrderStatus(order.getRoomMenuOrderStatus());
            dto.setRegDate(order.getRegDate() != null ? order.getRegDate() : order.getCreateDate());
            dto.setOriginalTotalPrice(order.getOriginalTotalPrice());
            dto.setDiscountedPrice(order.getDiscountedPrice());
            dto.setTotalPrice(order.getDiscountedPrice() != null ? order.getDiscountedPrice() : order.getOriginalTotalPrice());

            List<RoomMenuOrderItemDTO> itemDTOList = order.getOrderItems().stream().map(item -> {
                RoomMenuOrderItemDTO itemDTO = new RoomMenuOrderItemDTO();
                itemDTO.setRoomMenuOrderItemNum(item.getRoomMenuOrderItemNum().toString());
                itemDTO.setRoomMenuOrderItemAmount(item.getRoomMenuOrderAmount());
                itemDTO.setRoomMenuOrderItemPrice(item.getRoomMenuOrderPrice());
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName());
                itemDTO.setRoomMenuOrderRequestMessage(item.getRoomMenuOrderRequestMessage());

                // 이미지 정보 추가
                itemDTO.setRoomMenuImageMeta(item.getRoomMenu().getRoomMenuImageMeta());

                return itemDTO;
            }).collect(Collectors.toList());

            dto.setOrderItemList(itemDTOList);
            log.info("주문번호 {} -> regDate: {}", order.getRoomMenuOrderNum(), order.getRegDate());
            return dto;
        });
        return dtoPage;
    }

    /***********************************************
     * 메서드명 : cancelRoomMenuOrder
     * 기능 : 특정 주문 번호에 해당하는 주문을 취소한다.
     * - 주문을 조회하고, 주문한 회원의 이메일과 현재 로그인한 회원의 이메일을 비교하여 취소 권한을 확인한다.
     * - 이미 취소된 주문이거나, 주문 상태가 '주문'이 아닌 경우 예외를 발생시킨다.
     * - 주문 아이템들의 수량만큼 룸 메뉴의 재고를 복구한다.
     * - 주문 상태를 '취소'로 변경하고 저장한다.
     * 매개변수 : Long orderNum - 취소할 주문 번호
     * String email - 현재 로그인한 회원의 이메일
     * 반환값 : void
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-16
     * 수정일 : -
     * ***********************************************/

    // 주문 취소
    @Override
    public void cancelRoomMenuOrder(Long orderNum, String email) {
        log.info("주문 취소 서비스 진입 : " + email);
        RoomMenuOrder order = roomMenuOrderRepository.findById(orderNum)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        if (!order.getMember().getMemberEmail().equals(email)) {
            throw new IllegalStateException("본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getRoomMenuOrderStatus() == RoomMenuOrderStatus.CANCEL) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        if (order.getRoomMenuOrderStatus() != RoomMenuOrderStatus.ORDER) {
            throw new IllegalStateException("해당 주문은 취소할 수 없습니다.");
        }

        // 재고 복구
        for (RoomMenuOrderItem item : order.getOrderItems()) {
            RoomMenu menu = item.getRoomMenu();
            menu.restoreRoomMenuStockNumber(item.getRoomMenuOrderAmount());
        }

        // 상태 변경
        order.setRoomMenuOrderStatus(RoomMenuOrderStatus.CANCEL);

        roomMenuOrderRepository.save(order);
        log.info("주문취소 :: 삭제완료");

//         // 취소된 주문 삭제 (필요한 경우)
//         roomMenuOrderRepository.delete(order);  // 필요 시 전체 삭제
    }


    /***********************************************
     * 메서드명 : getAllOrdersForAdmin
     * 기능 : 관리자 페이지에서 모든 '주문' 상태의 주문 목록을 조회하여 DTO 리스트로 반환한다.
     * - 주문 상태가 '주문'인 모든 주문을 등록일자 내림차순으로 가져온다.
     * - 각 주문 정보를 RoomMenuOrderDTO로 변환하고, 주문 아이템 정보도 RoomMenuOrderItemDTO로 변환하여 포함한다.
     * - 주문한 회원 정보도 DTO에 포함한다.
     * 매개변수 : 없음
     * 반환값 : List<RoomMenuOrderDTO> - 주문 정보 DTO 리스트
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-16
     * 수정일 : -
     * ***********************************************/

    // 주문어드민
    @Override
    public Page<RoomMenuOrderDTO> getAllOrdersForAdmin(Pageable pageable) {
        log.info("관리자용 오더 승인 리스트 진입 (서비스)");
        // ORDER와 ACCEPT 상태인 주문 페이징 조회
        Page<RoomMenuOrder> orderPage = roomMenuOrderRepository.findAllByRoomMenuOrderStatusInOrderByRegDateDesc(
                Arrays.asList(RoomMenuOrderStatus.ORDER, RoomMenuOrderStatus.ACCEPT),
                pageable
        );

        // Page.map() 메서드를 이용해 DTO로 변환
        Page<RoomMenuOrderDTO> dtoPage = orderPage.map(order -> {
            RoomMenuOrderDTO dto = new RoomMenuOrderDTO();
            dto.setRoomMenuOrderNum(order.getRoomMenuOrderNum());
            dto.setRoomMenuOrderStatus(order.getRoomMenuOrderStatus());
            dto.setRegDate(order.getRegDate());
            dto.setOriginalTotalPrice(order.getOriginalTotalPrice());
            dto.setDiscountedPrice(order.getDiscountedPrice());
            dto.setMember(order.getMember());


            List<RoomMenuOrderItemDTO> itemDTOList = order.getOrderItems().stream().map(item -> {
                RoomMenuOrderItemDTO itemDTO = new RoomMenuOrderItemDTO();
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName());
                itemDTO.setRoomMenuOrderItemAmount(item.getRoomMenuOrderAmount());
                itemDTO.setRoomMenuOrderItemPrice(item.getRoomMenuOrderPrice());
                itemDTO.setRoomMenuOrderRequestMessage(item.getRoomMenuOrderRequestMessage());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setOrderItemList(itemDTOList);

            // ★ 총 금액 계산 부분: 주문 항목들의 (가격 * 수량) 합계를 구해서 totalPrice에 저장
            int total = order.getOrderItems().stream()
                    .mapToInt(item -> item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount())
                    .sum();
            dto.setTotalPrice(total);

            return dto;
        });

        return dtoPage;
    }

    @Override
    public void RoomMenuSendOrderAlert(RoomMenuOrderDTO orderDto, RoomMenuOrder order) {
        if (order == null || order.getMember() == null) {
            log.warn("알람 전송 실패: 주문 또는 회원 정보가 없음");
            return;
        }

        // 총 금액 계산 (RoomMenuOrder 내부 기준)
        int totalPrice = order.getOrderItems().stream()
                .mapToInt(item -> item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount())
                .sum();



        // RoomMenuOrderItemDTO 리스트 가져오기
        List<RoomMenuOrderItemDTO> itemDtoList = orderDto.getOrderItemList();
        log.info(" orderDto 전체 내용: {}", orderDto);
        log.info(" 주문 항목 리스트: {}", orderDto.getOrderItemList());

        String requestMessages = "";
        int totalAmount = 0;

        if (itemDtoList != null && !itemDtoList.isEmpty()) {
            requestMessages = itemDtoList.stream()
                    .map(RoomMenuOrderItemDTO::getRoomMenuOrderRequestMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", ")); // 줄바꿈 원하면 "\n"

            totalAmount = itemDtoList.stream()
                    .mapToInt(RoomMenuOrderItemDTO::getRoomMenuOrderItemAmount)
                    .sum();
        }

        // DTO 생성
        RoomMenuOrderAlertDTO alertDto = new RoomMenuOrderAlertDTO();
        alertDto.setMemberEmail(order.getMember().getMemberEmail());
        alertDto.setTotalPrice(totalPrice);
        alertDto.setRoomMenuOrderRequestMessage(requestMessages);
        alertDto.setRoomMenuOrderAmount(totalAmount);

        log.info("🚀 알람 전송 DTO: {}", alertDto);

        // 메시지 전송
        messagingTemplate.convertAndSend("/topic/new-order", alertDto);

    }



}