package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuOrderAlertDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderItemDTO;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.NotificationService;
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
    private final NotificationService notificationService;


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
        log.info(">>> roomMenuOrderInsertFromCart 메소드 진입 - 받은 discountedTotalPrice 파라미터: {}", discountedTotalPrice); // <-- 이 로그 추가

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
        roomMenuOrder.setRegDate(LocalDateTime.now()); // 주문 등록 시간 설정

        List<RoomMenuOrderItem> orderItems = new ArrayList<>();

        // 5. 장바구니 아이템 → 주문 아이템으로 변환
        for (RoomMenuCartItem cartItem : cartItems) {
            log.info("--- 장바구니 아이템 정보 확인 (주문 생성 시) ---");
            log.info("장바구니 아이템 ID: {}", cartItem.getRoomMenuCartItemNum());
            log.info("메뉴 이름 (장바구니에서): {}", cartItem.getRoomMenu().getRoomMenuName());
            log.info("장바구니 수량: {}", cartItem.getRoomMenuCartItemAmount());
            log.info("장바구니에서 가져온 옵션 이름: {}", cartItem.getRoomMenuSelectOptionName());
            log.info("장바구니에서 가져온 옵션 가격: {}", cartItem.getRoomMenuSelectOptionPrice()); // 옵션 가격 합계 (개당)
            log.info("RoomMenuCartItem Price (total for item line): {}", cartItem.getRoomMenuCartItemPrice()); // 아이템 라인 총 가격
            log.info("---------------------------------------");

            RoomMenu roomMenu = cartItem.getRoomMenu();

            // 재고 확인
            if (roomMenu.getRoomMenuAmount() < cartItem.getRoomMenuCartItemAmount()) {
                throw new IllegalStateException("재고가 부족한 메뉴가 존재합니다: " + roomMenu.getRoomMenuName());
            }

            // 주문 아이템 생성
            RoomMenuOrderItem orderItem = new RoomMenuOrderItem();
            orderItem.setRoomMenu(roomMenu);  // 주문할 메뉴 설정
            orderItem.setRoomMenuOrderAmount(cartItem.getRoomMenuCartItemAmount());  // 주문 수량 설정

            // RoomMenuOrderItemPrice 필드에 (기본가격 + 옵션가격 합계)를 설정 (개당 가격)
            // RoomMenuCartItemPrice / RoomMenuCartItemAmount 계산 시 소수점 문제가 발생할 수 있습니다.
            // RoomMenuCartItem에 개당 가격을 저장했거나, 여기서 다시 계산하는 로직이 필요할 수 있습니다.
            // 현재는 정수 나눗셈으로 진행합니다.
            int itemPricePerUnit = cartItem.getRoomMenuCartItemPrice() / cartItem.getRoomMenuCartItemAmount();
            orderItem.setRoomMenuOrderPrice(itemPricePerUnit);
            log.info("계산된 주문 아이템 개당 가격 (옵션 포함): {}", itemPricePerUnit);


            orderItem.setRoomMenuOrder(roomMenuOrder);  // 주문 객체와 연관관계 설정
            orderItem.setRoomMenuOrderRequestMessage(requestMessage); // 요청사항

            // 옵션 이름/가격도 주문 아이템에 복사 (요약 정보)
            orderItem.setRoomMenuSelectOptionName(cartItem.getRoomMenuSelectOptionName());
            orderItem.setRoomMenuSelectOptionPrice(cartItem.getRoomMenuSelectOptionPrice()); // RoomMenuCartItem의 옵션 가격 합계 (개당)

            // 재고 차감
            roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - cartItem.getRoomMenuCartItemAmount());
            // roomMenuRepository.save(roomMenu); // @Transactional로 자동 저장되거나 필요 시 명시적 저장


            orderItems.add(orderItem);
        }

        // 6. 주문 객체에 아이템 리스트 설정 (Order 엔티티에 CascadeType.ALL이 있으면 아이템도 함께 저장됨)
        roomMenuOrder.setOrderItems(orderItems);

        // 주문 총 금액 계산 및 설정 (아이템별 최종 가격 * 수량의 합계)
        int orderOriginalTotalPrice = orderItems.stream()
                .mapToInt(orderItem -> orderItem.getRoomMenuOrderPrice() * orderItem.getRoomMenuOrderAmount())
                .sum();
        roomMenuOrder.setOriginalTotalPrice(orderOriginalTotalPrice);
        log.info("계산된 주문 총 금액 (originalTotalPrice): {}", orderOriginalTotalPrice);

        // 할인된 최종 금액 설정 (쿠폰 사용 시)
        if (discountedTotalPrice != null) {
            roomMenuOrder.setDiscountedPrice(discountedTotalPrice);
            log.info("할인 적용된 최종 금액 (discountedPrice): {}", discountedTotalPrice);
        }

        // 사용된 쿠폰 번호 기록 (필요하다면)
        if (couponNum != null) {
            roomMenuOrder.setUsedCouponNum(couponNum);
            log.info("사용된 쿠폰 번호 기록: {}", couponNum);
        }


        // 7. >>> **RoomMenuOrder 엔티티 저장 (딱 한 번만 여기에)** <<<
        RoomMenuOrder savedOrder = roomMenuOrderRepository.save(roomMenuOrder);
        log.info("주문 엔티티 저장 완료. 주문 번호: {}", savedOrder.getRoomMenuOrderNum());


        // >>> **수정/추가된 로직 시작: 쿠폰 사용 처리 (주문 저장 후)** <<<
        // 이 로직은 주문이 성공적으로 데이터베이스에 저장된 후에 실행되어야 합니다.
        // (실제 시스템에서는 결제까지 완료된 후에 이 로직이 실행되는 것이 더 안전합니다.)
        if (couponNum != null) {
            log.info("주문 저장 후 쿠폰 사용 처리 시작. 쿠폰 번호: {}", couponNum);
            Coupon coupon = couponRepository.findById(couponNum)
                    .orElseThrow(() -> new EntityNotFoundException("쿠폰 정보를 찾을 수 없습니다.")); // 쿠폰 엔티티 다시 로드 (혹시 모를 상태 변경 대비)

            // 쿠폰 사용 처리 상세 로직
            if (coupon.isUsed()) { // 만약 이미 사용 완료된 쿠폰이라면 (동시성 문제 또는 로직 오류)
                // 반복 사용 쿠폰이고 사용 횟수가 0이하라면 예외 또는 경고
                if (coupon.getRepeatCouponCount() == null || coupon.getRepeatCouponCount() <= 0) {
                    log.warn("이미 사용 완료된 쿠폰({})을 주문에 다시 사용 시도 (로직 오류 가능성)", couponNum);
                    // throw new IllegalStateException("이미 사용된 쿠폰입니다."); // 필요시 예외 발생시켜 주문 취소 유도
                } else {
                    // 반복 쿠폰인데 사용 가능 횟수가 남았으므로 사용 처리 진행
                    log.info("반복 사용 쿠폰({}) 남은 횟수 있어 사용 처리 진행", couponNum);
                }

            }

            if (coupon.getRepeatCouponCount() != null) {
                // 반복 사용 쿠폰 횟수 차감
                coupon.setRepeatCouponCount(coupon.getRepeatCouponCount() - 1);
                log.info("쿠폰 {} 사용 횟수 1 차감. 남은 횟수: {}", coupon.getCouponNum(), coupon.getRepeatCouponCount());
                // 횟수 차감 후 0이 되면 사용 완료 처리
                if (coupon.getRepeatCouponCount() <= 0) {
                    coupon.setUsed(true);
                    log.info("쿠폰 {} 사용 횟수 소진, 사용 완료 처리.", coupon.getCouponNum());
                }
            } else {
                // 단일 사용 쿠폰
                coupon.setUsed(true); // 사용 완료 처리
                log.info("쿠폰 {} 단일 사용 완료 처리.", coupon.getCouponNum());
            }

            coupon.setUsedTime(LocalDateTime.now()); // 사용 시간 기록
            couponRepository.save(coupon); // **데이터베이스에 쿠폰 상태 변경 저장**
            log.info("쿠폰 {} 상태 변경 저장 완료.", couponNum);
        }
        // >>> **수정/추가된 로직 끝** <<<


        // 8. 장바구니 비우기
        roomMenuCartItemRepository.deleteAll(cartItems);
        log.info("장바구니 비우기 완료.");

        return savedOrder; // 저장된 주문 엔티티 반환
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

            // discountedPrice 값을 Entity에서 DTO로 복사
            dto.setDiscountedPrice(order.getDiscountedPrice()); // <-- Entity에서 DTO로 복사

            // >>> **추가된 로직 시작: Service Map 내부에서 DTO 필드 값 확인 (set 직후)** <<<
            log.info("--- Service Map: Debugging DTO after setting discountedPrice ---");
            // order.getDiscountedPrice()는 Entity에서 읽어온 값, dto.getDiscountedPrice()는 DTO에 설정된 값
            log.info("  OrderNum: {}, Entity DiscountedPrice: {}, DTO DiscountedPrice after set: {} (<<< set 직후 DTO 값)",
                    order.getRoomMenuOrderNum(), order.getDiscountedPrice(), dto.getDiscountedPrice()); // <-- 이 값을 확인합니다!
            log.info("------------------------------------------------------------");
            // >>> **추가된 로직 끝** <<<

            // 옵션 가격을 포함하여 originalTotal 계산
            int originalTotal = order.getOrderItems().stream()
                    .mapToInt(item -> {
                        // 아이템의 최종 가격 (기본 + 옵션) * 수량
                        return item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount();
                    })
                    .sum();
            dto.setOriginalTotalPrice(originalTotal); // 계산된 최종 총액을 DTO에 설정

            // 최종 결제 금액 계산 (할인 적용 여부에 따라)
            int totalPrice = 0;
            if (order.getDiscountedPrice() != null) {
                totalPrice = order.getDiscountedPrice();
            } else { // 할인 금액이 없으면 originalTotalPrice 사용
                totalPrice = originalTotal; // 수정된 originalTotal 사용
            }
            dto.setTotalPrice(totalPrice);

            List<RoomMenuOrderItemDTO> itemDTOList = order.getOrderItems().stream().map(item -> {
                RoomMenuOrderItemDTO itemDTO = new RoomMenuOrderItemDTO();
                itemDTO.setRoomMenuOrderItemNum(item.getRoomMenuOrderItemNum().toString());
                itemDTO.setRoomMenuOrderItemAmount(item.getRoomMenuOrderAmount());
                itemDTO.setRoomMenuOrderItemPrice(item.getRoomMenuOrderPrice());
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName());
                itemDTO.setRoomMenuOrderRequestMessage(item.getRoomMenuOrderRequestMessage());
                itemDTO.setRoomMenuSelectOptionName(item.getRoomMenuSelectOptionName());
                itemDTO.setRoomMenuSelectOptionPrice(item.getRoomMenuSelectOptionPrice());

                // 이미지 정보 추가
                itemDTO.setRoomMenuImageMeta(item.getRoomMenu().getRoomMenuImageMeta());

                return itemDTO;
            }).collect(Collectors.toList());

            dto.setOrderItemList(itemDTOList); // 아이템 리스트 설정 후

            // **수정 부분: 아이템 상세 정보 로그를 if 문 밖으로 이동**
            log.info("--- 로그를 찍어 디버깅 ---");
            log.info("오더의 넘버: {}", dto.getRoomMenuOrderNum());
            log.info("오리지널 가격: {}", originalTotal); // 서비스에서 계산한 값
            log.info("오리지널 합산 가격: {}", dto.getOriginalTotalPrice()); // DTO에 담긴 값
            log.info("할인된 금액: {}", dto.getDiscountedPrice());
            log.info("총 금액: {}", dto.getTotalPrice()); // 최종 표시될 값


            if (dto.getOrderItemList() != null) { // 아이템 리스트가 null이 아닌 경우에만 출력
                log.info("--- Order Items ---");
                for (RoomMenuOrderItemDTO itemDTO : dto.getOrderItemList()) {
                    log.info("  아이템 이름: {}", itemDTO.getRoomMenuOrderItemName());
                    log.info("  아이템 가격: {}", itemDTO.getRoomMenuOrderItemPrice());
                    log.info("  아이템 수량: {}", itemDTO.getRoomMenuOrderItemAmount());
                    log.info("  옵션 이름 :  {}", itemDTO.getRoomMenuSelectOptionName());
                    log.info("  옵션 가격: {}", itemDTO.getRoomMenuSelectOptionPrice());
                    int itemCalculatedPrice = itemDTO.getRoomMenuOrderItemPrice() + (itemDTO.getRoomMenuSelectOptionPrice() != null ? itemDTO.getRoomMenuSelectOptionPrice() : 0);
                    log.info("  합산된 금액 (with option): {}", itemCalculatedPrice);
                    log.info("  합산된 총 금액: {}", itemCalculatedPrice * itemDTO.getRoomMenuOrderItemAmount());
                    log.info("  ---");
                }
                log.info("-----------------");
            }

            log.info("주문번호 {} -> regDate: {}", order.getRoomMenuOrderNum(), dto.getRegDate()); // dto.getRegDate()로 변경
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
            dto.setDiscountedPrice(order.getDiscountedPrice());
            dto.setMember(order.getMember());
            // DTO의 originalTotalPrice도 계산하여 설정 (일관성을 위해)
            int originalTotal = order.getOrderItems().stream()
                    .mapToInt(item -> item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount())
                    .sum();
            dto.setOriginalTotalPrice(originalTotal);
            // DTO의 totalPrice도 설정 (관리자 페이지에서 사용한다면)
            int totalPrice = originalTotal;
            if (order.getDiscountedPrice() != null) {
                totalPrice = order.getDiscountedPrice();
            }
            dto.setTotalPrice(totalPrice);


            List<RoomMenuOrderItemDTO> itemDTOList = order.getOrderItems().stream().map(item -> {
                RoomMenuOrderItemDTO itemDTO = new RoomMenuOrderItemDTO();
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName());
                itemDTO.setRoomMenuOrderItemAmount(item.getRoomMenuOrderAmount());
                itemDTO.setRoomMenuOrderItemPrice(item.getRoomMenuOrderPrice());
                itemDTO.setRoomMenuOrderRequestMessage(item.getRoomMenuOrderRequestMessage());
                // >>> **추가: 옵션 필드 값을 DTO에 복사** <<<
                itemDTO.setRoomMenuSelectOptionName(item.getRoomMenuSelectOptionName());
                itemDTO.setRoomMenuSelectOptionPrice(item.getRoomMenuSelectOptionPrice());
                // >>> **추가 완료** <<<
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setOrderItemList(itemDTOList);

            log.info("관리자 오더 DTO 변환 확인 - 주문번호: {}", dto.getRoomMenuOrderNum());
            if (!dto.getOrderItemList().isEmpty()) {
                log.info("  첫 번째 아이템 옵션 이름: {}", dto.getOrderItemList().get(0).getRoomMenuSelectOptionName());
                log.info("  첫 번째 아이템 옵션 가격: {}", dto.getOrderItemList().get(0).getRoomMenuSelectOptionPrice());
            }


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

        // --- 주문 ID 가져오기 ---
        Long orderId = order.getRoomMenuOrderNum(); // RoomMenuOrder 엔티티에 getId() 메소드가 있다고 가정
        if (orderId == null) {
            log.warn("알람 전송 실패: 주문 ID를 가져올 수 없음");
            return;
        }
        // --- 주문 ID 가져오기 끝 ---

        // 총 금액 계산 (RoomMenuOrder 내부 기준)
        int totalPrice = order.getOrderItems().stream()
                .mapToInt(item -> item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount())
                .sum();

        // RoomMenuOrderItemDTO 리스트 가져오기 및 정보 추출
        List<RoomMenuOrderItemDTO> itemDtoList = orderDto.getOrderItemList();
        log.info(" orderDto 전체 내용: {}", orderDto);
        log.info(" 주문 항목 리스트: {}", orderDto.getOrderItemList());

        String requestMessages = "";
        int totalAmount = 0;
        String memberEmail = order.getMember().getMemberEmail(); // 주문자 이메일

        if (itemDtoList != null && !itemDtoList.isEmpty()) {
            requestMessages = itemDtoList.stream()
                    .map(RoomMenuOrderItemDTO::getRoomMenuOrderRequestMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));

            totalAmount = itemDtoList.stream()
                    .mapToInt(RoomMenuOrderItemDTO::getRoomMenuOrderItemAmount)
                    .sum();
        }

        // === 수정 시작: 알림 저장 및 전송 DTO 재구성 ===

        // 1. 알림 저장에 필요한 정보를 담을 DTO 준비 (기존 DTO 활용)
        RoomMenuOrderAlertDTO alertInfoForDb = new RoomMenuOrderAlertDTO();
        alertInfoForDb.setMemberEmail(memberEmail);
        alertInfoForDb.setTotalPrice(totalPrice);
        // 필요 시 추가 정보 설정

        // 2. NotificationService를 호출하여 알림을 DB에 저장하고 결과 받기
        //    (NotificationService가 주입되어 있어야 함)
        Notification savedNotification = notificationService.createAndSaveNewOrderNotification(orderId, alertInfoForDb);

        // 3. WebSocket으로 전송할 최종 DTO 생성 (Builder 사용 및 추가 정보 설정)
        //    (RoomMenuOrderAlertDTO에 notificationId, orderId, orderTimestamp 필드가 추가되어 있어야 함)
        RoomMenuOrderAlertDTO alertDtoForWebSocket = RoomMenuOrderAlertDTO.builder()
                .memberEmail(memberEmail)
                .totalPrice(totalPrice)
                .roomMenuOrderRequestMessage(requestMessages)
                .roomMenuOrderAmount(totalAmount)
                .notificationId(savedNotification.getNotificationId()) // 저장된 알림 ID 설정
                .orderId(orderId)                           // 주문 ID 설정
                .orderTimestamp(savedNotification.getCreateDate()) // 알림 생성 시간 (BaseEntity 상속)
                .build();

        log.info("🚀 최종 WebSocket 알람 전송 DTO: {}", alertDtoForWebSocket);

        // 4. 수정된 최종 DTO를 WebSocket으로 전송
        messagingTemplate.convertAndSend("/topic/new-order", alertDtoForWebSocket);

        // === 수정 끝 ===
    }
}