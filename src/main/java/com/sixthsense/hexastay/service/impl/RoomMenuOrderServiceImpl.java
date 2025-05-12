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
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final RoomRepository roomRepository;
    private final RoomMenuOptionRepository roomMenuOptionRepository;


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
        log.info("룸 메뉴 오더 서비스 주문처리 진입. Email: {}, DTO: {}", email, roomMenuOrderDTO);
        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuOrderDTO.getRoomMenuOrderNum())
                .orElseThrow(() -> new EntityNotFoundException("상품 ID " + roomMenuOrderDTO.getRoomMenuOrderNum() + "를 찾을 수 없습니다."));
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) { throw new EntityNotFoundException("회원 Email " + email + "을 찾을 수 없습니다."); }

        int productOrderQuantity = roomMenuOrderDTO.getRoomMenuOrderAmount();

        // 0원 상품 제한
        if (roomMenu.getRoomMenuPrice() == 0 && productOrderQuantity > 1) {
            throw new IllegalStateException("'" + roomMenu.getRoomMenuName() + "' 상품은 가격이 0원이므로 1개만 주문할 수 있습니다.");
        }
        // 상품 재고 확인
        if (roomMenu.getRoomMenuAmount() < productOrderQuantity) {
            throw new IllegalArgumentException("요청 수량이 현재 재고보다 많습니다. (상품: '" + roomMenu.getRoomMenuName() + "', 현재수량 : " + roomMenu.getRoomMenuAmount() + ")");
        }

        // --- ✅ 단일 옵션 재고 차감 로직 (RoomMenuOrderDTO에 옵션 ID와 옵션 수량 필드가 있다고 가정) ---
        Long selectedOptionIdFromDto = roomMenuOrderDTO.getRoomMenuOrderNum(); // DTO에 이 필드 추가 필요
        Integer optionOrderQuantityFromDto = roomMenuOrderDTO.getRoomMenuOrderAmount(); // DTO에 이 필드 추가 필요

        RoomMenuOption optionToRecordInOrderItem = null; // 주문 항목에 기록할 최종 옵션 정보

        if (selectedOptionIdFromDto != null && selectedOptionIdFromDto > 0 &&
                optionOrderQuantityFromDto != null && optionOrderQuantityFromDto > 0) {

            RoomMenuOption selectedOption = roomMenuOptionRepository.findById(selectedOptionIdFromDto)
                    .orElseThrow(() -> new EntityNotFoundException("선택된 옵션(ID: " + selectedOptionIdFromDto + ")을 찾을 수 없습니다."));

            if (!selectedOption.getRoomMenu().getRoomMenuNum().equals(roomMenu.getRoomMenuNum())) {
                throw new IllegalStateException("선택된 옵션이 해당 상품의 옵션이 아닙니다.");
            }

            if (selectedOption.getRoomMenuOptionAmount() < optionOrderQuantityFromDto) {
                throw new IllegalStateException("옵션 '" + selectedOption.getRoomMenuOptionName() + "'의 재고가 부족합니다. (요청: " + optionOrderQuantityFromDto + ", 현재: " + selectedOption.getRoomMenuOptionAmount() +")");
            }
            selectedOption.setRoomMenuOptionAmount(selectedOption.getRoomMenuOptionAmount() - optionOrderQuantityFromDto);
             roomMenuOptionRepository.save(selectedOption);
            optionToRecordInOrderItem = selectedOption;
            log.info("옵션 '{}' (ID: {}) 재고 {} 차감 완료. 남은 재고: {}",
                    selectedOption.getRoomMenuOptionName(), selectedOption.getRoomMenuOptionNum(),
                    optionOrderQuantityFromDto, selectedOption.getRoomMenuOptionAmount());
        }
        // --- 옵션 재고 차감 끝 ---

        RoomMenuOrder roomMenuOrder = new RoomMenuOrder();
        roomMenuOrder.setMember(member);
        roomMenuOrder.setRoomMenuOrderStatus(RoomMenuOrderStatus.ORDER);
        roomMenuOrder.setRegDate(LocalDateTime.now());

        List<RoomMenuOrderItem> roomMenuOrderItemList = new ArrayList<>();
        RoomMenuOrderItem roomMenuOrderItem = new RoomMenuOrderItem();
        roomMenuOrderItem.setRoomMenu(roomMenu);
        roomMenuOrderItem.setRoomMenuOrderAmount(productOrderQuantity);

        // 주문 아이템 가격: 상품 가격 + (선택된 경우) 옵션 가격
        int itemBasePrice = roomMenu.getRoomMenuPrice();
        int itemFinalPrice = itemBasePrice;
        if (optionToRecordInOrderItem != null) {
            itemFinalPrice += optionToRecordInOrderItem.getRoomMenuOptionPrice(); // 옵션 단가 추가
            roomMenuOrderItem.setRoomMenuSelectOptionName(optionToRecordInOrderItem.getRoomMenuOptionName());
            roomMenuOrderItem.setRoomMenuSelectOptionPrice(optionToRecordInOrderItem.getRoomMenuOptionPrice());
        }
        roomMenuOrderItem.setRoomMenuOrderPrice(itemFinalPrice); // (상품단가 + 옵션단가) 저장

        roomMenuOrderItem.setRoomMenuOrder(roomMenuOrder);
        roomMenuOrderItemList.add(roomMenuOrderItem);
        roomMenuOrder.setOrderItems(roomMenuOrderItemList);

        roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - productOrderQuantity);
        roomMenuRepository.save(roomMenu);


        int orderOriginalTotalPrice = roomMenuOrderItemList.stream()
                .mapToInt(oi -> oi.getRoomMenuOrderPrice() * oi.getRoomMenuOrderAmount())
                .sum();
        roomMenuOrder.setOriginalTotalPrice(orderOriginalTotalPrice);

        RoomMenuOrder savedRoomMenuOrder = roomMenuOrderRepository.save(roomMenuOrder);
        return savedRoomMenuOrder.getRoomMenuOrderNum();
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

    public RoomMenuOrder roomMenuOrderInsertFromCart(String email, String requestMessage,
                                                     Long couponNum, Integer discountedTotalPrice,
                                                     Pageable pageable, String password) {
        log.info("장바구니 기반 주문 생성 시작 - email: {}", email);

        // 1. 로그인한 회원 조회
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) {
            throw new EntityNotFoundException("회원 Email " + email + "을 찾을 수 없습니다.");
        }

        // --- 현재 시간 기준으로 사용자의 활성 Room 찾기 ---
        LocalDateTime now = LocalDateTime.now();
        Room currentActiveRoom = roomRepository.findActiveRoomsOrdered(member, now)
                .stream()
                .filter(r -> r.getRoomPassword() != null && r.getRoomPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 일치하는 룸이 아니거나, 현재 체크인 된 상태가 아니거나, 체크만료된 객실입니다.."));
        HotelRoom associatedHotelRoom = currentActiveRoom.getHotelRoom();
        log.info("현재 활성 객실 정보 조회 완료: Room Num {}, HotelRoom Name {}",
                currentActiveRoom.getRoomNum(),
                associatedHotelRoom != null ? associatedHotelRoom.getHotelRoomName() : "연결된 HotelRoom 없음");

        // 2. 해당 회원의 장바구니 가져오기
        RoomMenuCart cart = roomMenuCartRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 존재하지 않습니다."));

        // 3. 장바구니 아이템들 가져오기
        List<RoomMenuCartItem> cartItems = roomMenuCartItemRepository.findByRoomMenuCart(cart);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니에 아이템이 존재하지 않습니다.");
        }

        // 4. 주문 객체 생성 및 관계 설정
        RoomMenuOrder roomMenuOrder = new RoomMenuOrder();
        roomMenuOrder.setMember(member);
        roomMenuOrder.setRoomMenuOrderStatus(RoomMenuOrderStatus.ORDER);
        roomMenuOrder.setRegDate(LocalDateTime.now());
        roomMenuOrder.setRoom(currentActiveRoom);
        if (associatedHotelRoom != null) {
            roomMenuOrder.setHotelRoom(associatedHotelRoom);
        } else {
            log.error("!!! 데이터 문제: Room ID {}에 HotelRoom 정보가 연결되지 않았습니다.", currentActiveRoom.getRoomNum());
            throw new IllegalStateException("객실 기본 정보(HotelRoom)가 누락되어 주문을 진행할 수 없습니다.");
        }

        List<RoomMenuOrderItem> orderItems = new ArrayList<>();

        // 5. 장바구니 아이템 → 주문 아이템으로 변환
        for (RoomMenuCartItem cartItem : cartItems) {
            RoomMenu roomMenu = cartItem.getRoomMenu();
            if (roomMenu == null) {
                throw new EntityNotFoundException("장바구니 아이템(ID: " + cartItem.getRoomMenuCartItemNum() + ")에 연결된 상품 정보가 없습니다.");
            }
            int productOrderQuantity = cartItem.getRoomMenuCartItemAmount();

            // 0원 상품 제한
            if (roomMenu.getRoomMenuPrice() == 0 && productOrderQuantity > 1) {
                log.warn("0원 상품 '{}' 주문 수량 제한 위반 (장바구니). 요청 수량: {}", roomMenu.getRoomMenuName(), productOrderQuantity);
                throw new IllegalStateException("'" + roomMenu.getRoomMenuName() + "' 상품은 가격이 0원이므로 1개만 주문할 수 있습니다.");
            }

            // 상품 재고 확인
            if (roomMenu.getRoomMenuAmount() < productOrderQuantity) {
                throw new IllegalStateException("상품 '" + roomMenu.getRoomMenuName() + "'의 재고가 부족합니다. (요청: " + productOrderQuantity + ", 현재: " + roomMenu.getRoomMenuAmount() + ")");
            }

            // 상품 재고 차감
            roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - productOrderQuantity);

            // --- 선택된 옵션들의 재고 차감 로직 ---
            StringBuilder selectedOptionsNameBuilder = new StringBuilder();
            int totalOptionsPriceForThisItem = 0; // 이 상품 아이템에 대한 총 옵션 추가 가격

            if (cartItem.getRoomMenuCartItemOptions() != null && !cartItem.getRoomMenuCartItemOptions().isEmpty()) {
                for (RoomMenuCartItemOption cartItemOption : cartItem.getRoomMenuCartItemOptions()) {
                    RoomMenuOption actualOptionToDeduct = cartItemOption.getRoomMenuOption(); // RoomMenuCartItemOption이 RoomMenuOption을 직접 참조한다고 가정

                    if (actualOptionToDeduct == null) {
                        log.warn("장바구니의 옵션 항목(CartItemOption PK: {})에 연결된 실제 옵션 정보(RoomMenuOption)가 없습니다. 재고 차감을 건너뜁니다.", cartItemOption.getRoomMenuCartItemOptionNum());
                        continue;
                    }

                    // 옵션의 주문 수량: RoomMenuCartItemOption의 roomMenuCartItemOptionAmount 필드 사용
                    int orderedOptionQuantity = cartItemOption.getRoomMenuCartItemOptionAmount();
                    if (orderedOptionQuantity <= 0) { // 주문 수량이 0 이하면 처리하지 않음
                        log.info("옵션 '{}'의 주문 수량이 0 이하이므로 건너<0xE1><0xB9><0xA5>니다.", actualOptionToDeduct.getRoomMenuOptionName());
                        continue;
                    }


                    log.info("상품 '{}'의 옵션 '{}' (ID: {}) 재고 차감 시도. 선택된 옵션 수량: {}",
                            roomMenu.getRoomMenuName(), actualOptionToDeduct.getRoomMenuOptionName(),
                            actualOptionToDeduct.getRoomMenuOptionNum(), orderedOptionQuantity);

                    // 옵션 재고 확인
                    if (actualOptionToDeduct.getRoomMenuOptionAmount() < orderedOptionQuantity) {
                        throw new IllegalStateException("옵션 '" + actualOptionToDeduct.getRoomMenuOptionName() + "'의 재고가 부족합니다. (현재 재고: " + actualOptionToDeduct.getRoomMenuOptionAmount() + ", 요청: " + orderedOptionQuantity + ")");
                    }

                    // 옵션 재고 차감
                    actualOptionToDeduct.setRoomMenuOptionAmount(actualOptionToDeduct.getRoomMenuOptionAmount() - orderedOptionQuantity);
                     roomMenuOptionRepository.save(actualOptionToDeduct);

                    log.info("옵션 '{}' (ID: {}) 재고 {} 차감 완료. 남은 재고: {}",
                            actualOptionToDeduct.getRoomMenuOptionName(), actualOptionToDeduct.getRoomMenuOptionNum(),
                            orderedOptionQuantity, actualOptionToDeduct.getRoomMenuOptionAmount());

                    // RoomMenuOrderItem에 기록할 옵션 이름 및 가격 정보 누적
                    if (selectedOptionsNameBuilder.length() > 0) {
                        selectedOptionsNameBuilder.append(", ");
                    }
                    selectedOptionsNameBuilder.append(actualOptionToDeduct.getRoomMenuOptionName())
                            .append(" (").append(orderedOptionQuantity).append("개)");
                    totalOptionsPriceForThisItem += (actualOptionToDeduct.getRoomMenuOptionPrice() * orderedOptionQuantity);
                }
            }

            // --- 옵션 재고 차감 로직 끝 ---

            // 주문 아이템 생성
            RoomMenuOrderItem orderItem = new RoomMenuOrderItem();
            orderItem.setRoomMenu(roomMenu);
            orderItem.setRoomMenuOrderAmount(productOrderQuantity);

            int baseItemPricePerUnit = roomMenu.getRoomMenuPrice(); // 상품 순수 단가

            int itemPricePerUnitWithOptions = cartItem.getRoomMenuCartItemPrice() / productOrderQuantity;
            orderItem.setRoomMenuOrderPrice(itemPricePerUnitWithOptions);
            log.info("주문 아이템 '{}'의 최종 개당 가격 (옵션포함): {}", roomMenu.getRoomMenuName(), itemPricePerUnitWithOptions);


            orderItem.setRoomMenuOrder(roomMenuOrder);
            orderItem.setRoomMenuOrderRequestMessage(requestMessage);

            // RoomMenuOrderItem에 옵션 정보 요약 기록
            if (selectedOptionsNameBuilder.length() > 0) {
                orderItem.setRoomMenuSelectOptionName(selectedOptionsNameBuilder.toString());
                // RoomMenuOrderItem의 roomMenuSelectOptionPrice는 여러 옵션의 합계 가격을 나타내도록 수정 필요.
                // 여기서는 totalOptionsPriceForThisItem / productOrderQuantity (개당 총 옵션 추가금) 또는
                // cartItem에 저장된 요약 옵션 가격을 사용.
                orderItem.setRoomMenuSelectOptionPrice(totalOptionsPriceForThisItem / productOrderQuantity); // 개당 총 옵션 추가금으로 설정
            } else {
                // 옵션이 없는 경우, cartItem의 기본값 사용 (null일 수 있음)
                orderItem.setRoomMenuSelectOptionName(cartItem.getRoomMenuSelectOptionName());
                orderItem.setRoomMenuSelectOptionPrice(cartItem.getRoomMenuSelectOptionPrice());
            }

            roomMenuRepository.save(roomMenu);
            orderItems.add(orderItem);
        } // end of for (RoomMenuCartItem cartItem : cartItems)



        roomMenuOrder.setOrderItems(orderItems);

        // 주문 총 금액 계산 및 설정
        // RoomMenuOrder 엔티티에 이 로직을 넣는 것이 좋음: roomMenuOrder.calculateAndSetTotalPrice();
        int orderOriginalTotalPrice = orderItems.stream()
                .mapToInt(oi -> oi.getRoomMenuOrderPrice() * oi.getRoomMenuOrderAmount())
                .sum();
        roomMenuOrder.setOriginalTotalPrice(orderOriginalTotalPrice);
        log.info("계산된 주문 총 금액 (originalTotalPrice): {}", orderOriginalTotalPrice);

        if (discountedTotalPrice != null) {
            roomMenuOrder.setDiscountedPrice(discountedTotalPrice);
            log.info("할인 적용된 최종 금액 (discountedPrice): {}", discountedTotalPrice);
        }

        if (couponNum != null) {
            roomMenuOrder.setUsedCouponNum(couponNum);
            log.info("사용된 쿠폰 번호 기록: {}", couponNum);
        }

        RoomMenuOrder savedOrder = roomMenuOrderRepository.save(roomMenuOrder);
        log.info("주문 엔티티 저장 완료. 주문 번호: {}, 연결된 Room 번호: {}, 연결된 객실 이름: {}",
                savedOrder.getRoomMenuOrderNum(),
                savedOrder.getRoom() != null ? savedOrder.getRoom().getRoomNum() : "연결된 Room 없음",
                savedOrder.getHotelRoom() != null ? savedOrder.getHotelRoom().getHotelRoomName() : "연결된 HotelRoom 없음");

        if (couponNum != null) {
            if (couponNum != null && couponNum > 0) { // couponNum 유효성 체크 추가
                log.info("주문 저장 후 쿠폰 사용 처리 시작. 쿠폰 번호: {}", couponNum);
                try {
                    // 쿠폰 정보를 다시 조회하여 최신 상태 확인 (동시성 문제 방지)
                    Coupon coupon = couponRepository.findById(couponNum)
                            .orElseThrow(() -> new EntityNotFoundException("쿠폰 정보를 찾을 수 없습니다. CouponNum: " + couponNum));

                    // 쿠폰 유효성 재검증 (주문 생성 중 상태가 변경될 수 있으므로)
                    if (!coupon.getMember().getMemberEmail().equals(email)) {
                        throw new IllegalStateException("쿠폰 소유자가 일치하지 않습니다.");
                    }
                    if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
                        throw new IllegalStateException("쿠폰이 만료되었습니다.");
                    }

                    // 사용 처리 로직
                    if (coupon.isUsed()) {
                        if (coupon.getRepeatCouponCount() != null && coupon.getRepeatCouponCount() > 0) {
                            // 반복 쿠폰이고 횟수가 남아있음
                            coupon.setRepeatCouponCount(coupon.getRepeatCouponCount() - 1);
                            log.info("반복 쿠폰(ID:{}) 사용, 남은 횟수: {}", couponNum, coupon.getRepeatCouponCount());
                            if (coupon.getRepeatCouponCount() <= 0) {
                                coupon.setUsed(true); // 횟수 소진 시 사용 완료 처리
                                log.info("반복 쿠폰(ID:{}) 횟수 소진으로 사용 완료 처리.", couponNum);
                            }
                        } else {
                            log.warn("이미 사용 완료 처리된 쿠폰(ID:{})에 대한 주문(ID:{})이 생성되었습니다. (확인 필요)", couponNum, savedOrder.getRoomMenuOrderNum());
                        }
                    } else {

                        if (coupon.getRepeatCouponCount() != null) {

                            coupon.setRepeatCouponCount(coupon.getRepeatCouponCount() - 1);
                            log.info("반복 쿠폰(ID:{}) 사용, 남은 횟수: {}", couponNum, coupon.getRepeatCouponCount());
                            if (coupon.getRepeatCouponCount() <= 0) {
                                coupon.setUsed(true); // 횟수 소진 시 사용 완료 처리
                                log.info("반복 쿠폰(ID:{}) 횟수 소진으로 사용 완료 처리.", couponNum);
                            }
                        } else {
                            // 단일 사용 쿠폰
                            coupon.setUsed(true); // 사용 완료 처리
                            log.info("단일 사용 쿠폰(ID:{}) 사용 완료 처리.", couponNum);
                        }
                    }

                    coupon.setUsedTime(LocalDateTime.now()); // 사용 시간 기록
                    couponRepository.save(coupon); //
                    log.info("쿠폰(ID:{}) 상태 변경 저장 완료.", couponNum);

                } catch (Exception e) {
                    log.error("주문(ID:{}) 생성 후 쿠폰(ID:{}) 처리 중 오류 발생: {}", savedOrder.getRoomMenuOrderNum(), couponNum, e.getMessage(), e);
                }
            }
        }

        roomMenuCartItemRepository.deleteAll(cartItems); // 장바구니 아이템 삭제
        log.info("장바구니 비우기 완료.");

        return savedOrder;
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
                itemDTO.setRoomMenuOrderItemNum(item.getRoomMenuOrderItemNum());
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
            dto.setMember(order.getMember());

            if (order.getHotelRoom() != null) {
                dto.setHotelRoomName(order.getHotelRoom().getHotelRoomName());
            } else {
                dto.setHotelRoomName("정보 없음"); // HotelRoom 정보가 없는 경우
            }

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
                itemDTO.setRoomMenuOrderItemName(item.getRoomMenu().getRoomMenuName()); /* 룸의 이름 표시 */
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
    public void RoomMenuSendOrderAlert(RoomMenuOrderDTO orderDto, RoomMenuOrder order, Pageable pageable) {


        if (order == null || order.getMember() == null) {
            log.warn("알람 전송 실패: 주문 또는 회원 정보가 없음");
            return;
        }
        Long orderId = order.getRoomMenuOrderNum();
        if (orderId == null) {
            log.warn("알람 전송 실패: 주문 ID를 가져올 수 없음");
            return;
        }

        // --- 수정: 올바른 hotelRoomName 가져오기 ---

        String hotelRoomName = "객실 정보 없음";
        try {
            log.info(">>> 호텔 객실 이름 가져오기 시도...");
            if (order.getRoom() != null && order.getRoom().getHotelRoom() != null) {
                hotelRoomName = order.getRoom().getHotelRoom().getHotelRoomName();
                // ... (null/empty 체크) ...
            }
            log.info(">>> 호텔 객실 이름: {}", hotelRoomName);
        } catch (Exception e) {
            log.error(">>> 호텔 객실 이름 가져오기 중 오류!", e); throw e;
        }

        // --- 수정 끝 ---



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

        RoomMenuOrderAlertDTO alertInfoForDb = new RoomMenuOrderAlertDTO();
        alertInfoForDb.setMemberEmail(memberEmail);
        alertInfoForDb.setTotalPrice(totalPrice);
        alertInfoForDb.setHotelRoomName(hotelRoomName);


        Notification savedNotification = notificationService.createAndSaveNewOrderNotification(orderId, alertInfoForDb);

        RoomMenuOrderAlertDTO alertDtoForWebSocket = RoomMenuOrderAlertDTO.builder()

                .memberEmail(memberEmail)
                .totalPrice(totalPrice)
                .roomMenuOrderRequestMessage(requestMessages)
                .roomMenuOrderAmount(totalAmount)
                .notificationId(savedNotification.getNotificationId()) // 저장된 알림 ID 설정
                .orderId(orderId)                           // 주문 ID 설정
                .orderTimestamp(savedNotification.getCreateDate()) // 알림 생성 시간 (BaseEntity 상속)
                .hotelRoomName(hotelRoomName)
                .build();

        log.info("🚀 최종 WebSocket 알람 전송 DTO: {}", alertDtoForWebSocket);



        // 4. 수정된 최종 DTO를 WebSocket으로 전송
        messagingTemplate.convertAndSend("/topic/new-order", alertDtoForWebSocket);

        // === 수정 끝 ===
    }

    // 주문 접수시 알람 추가
    @Override
    public RoomMenuOrder acceptOrder(Long orderId) {
        log.info("주문 접수 서비스 알람 - orderId: {}", orderId);
        RoomMenuOrder order = roomMenuOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID " + orderId + "에 해당하는 주문을 찾을 수 없습니다."));

        order.setRoomMenuOrderStatus(RoomMenuOrderStatus.ACCEPT);
        return roomMenuOrderRepository.save(order);
    }

    // 주문 완료시 알람 추가
    @Override
    public RoomMenuOrder completeOrder(Long orderId) {
        log.info("주문 완료 서비스 알람 - orderId: {}", orderId);

        RoomMenuOrder order = roomMenuOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID " + orderId + "에 해당하는 주문을 찾을 수 없습니다."));

        order.setRoomMenuOrderStatus(RoomMenuOrderStatus.COMPLETE);
        return roomMenuOrderRepository.save(order);
    }


    // 주문 취소시 알람 추가
    @Override
    public RoomMenuOrder cancelOrderAsAdmin(Long orderId) {
        log.info("관리자 주문 취소 서비스 알람 - orderId: {}", orderId);
        RoomMenuOrder order = roomMenuOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 ID " + orderId + "에 해당하는 주문을 찾을 수 없습니다."));

        for (RoomMenuOrderItem item : order.getOrderItems()) {
            RoomMenu menu = item.getRoomMenu();
            log.info("재고 복원 시도: 메뉴 ID {}, 수량 {}", menu.getRoomMenuNum(), item.getRoomMenuOrderAmount());
        }

        order.setRoomMenuOrderStatus(RoomMenuOrderStatus.CANCEL);
        return roomMenuOrderRepository.save(order);
    }

    @Override
    public void deductOptionStock(RoomMenu roomMenu, RoomMenuOrderItemDTO orderItemDTO, int orderedItemQuantity) {
        Long selectedOptionNum = orderItemDTO.getRoomMenuOrderItemNum(); // DTO에 이 필드가 있어야 함!

        if (selectedOptionNum != null && selectedOptionNum > 0) {
            log.info("상품 '{}'에 대한 옵션 ID {} 재고 차감 시도. 주문 수량: {}", roomMenu.getRoomMenuName(), selectedOptionNum, orderedItemQuantity);

            RoomMenuOption selectedOption = roomMenuOptionRepository.findById(selectedOptionNum)
                    .orElseThrow(() -> new EntityNotFoundException("선택된 옵션(ID: " + selectedOptionNum + ")을 찾을 수 없습니다."));

            // 선택된 옵션이 해당 메뉴(roomMenu)에 속하는지 추가 검증 (선택 사항이지만 안전)
            if (!selectedOption.getRoomMenu().getRoomMenuNum().equals(roomMenu.getRoomMenuNum())) {
                log.error("주문된 상품 ID {}과 옵션 {}의 부모 상품 ID {}가 일치하지 않습니다.",
                        roomMenu.getRoomMenuNum(), selectedOptionNum, selectedOption.getRoomMenu().getRoomMenuNum());
                throw new IllegalStateException("선택된 옵션이 해당 상품의 옵션이 아닙니다.");
            }

            // 옵션 재고 확인
            if (selectedOption.getRoomMenuOptionAmount() < orderedItemQuantity) {
                log.warn("옵션 '{}' (ID: {}) 재고 부족. 현재 재고: {}, 요청 수량: {}",
                        selectedOption.getRoomMenuOptionName(), selectedOptionNum, selectedOption.getRoomMenuOptionAmount(), orderedItemQuantity);
                throw new IllegalStateException("옵션 '" + selectedOption.getRoomMenuOptionName() + "'의 재고가 부족합니다. (현재 재고: " + selectedOption.getRoomMenuOptionAmount() + ")");
            }

            // 옵션 재고 차감
            selectedOption.setRoomMenuOptionAmount(selectedOption.getRoomMenuOptionAmount() - orderedItemQuantity);
            roomMenuOptionRepository.save(selectedOption); // 변경된 옵션 정보 저장
            log.info("옵션 '{}' (ID: {}) 재고 {} 차감 완료. 남은 재고: {}",
                    selectedOption.getRoomMenuOptionName(), selectedOptionNum,
                    orderedItemQuantity, selectedOption.getRoomMenuOptionAmount());
        } else {
            log.info("상품 '{}'에 대해 선택된 옵션이 없거나 옵션 ID가 유효하지 않습니다. 옵션 재고 차감을 건너뜁니다.", roomMenu.getRoomMenuName());
        }
    }


}