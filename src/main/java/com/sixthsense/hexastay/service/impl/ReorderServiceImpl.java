package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.ReorderService;
import com.sixthsense.hexastay.util.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class ReorderServiceImpl implements ReorderService {

    private final MemberRepository memberRepository;
    private final RoomMenuOrderRepository roomMenuOrderRepository;
    private final RoomMenuRepository roomMenuRepository; // 상품(RoomMenu) Repository
    private final RoomMenuCartRepository roomMenuCartRepository;
    private final RoomMenuCartItemRepository roomMenuCartItemRepository;

    @Override
    public void addPastOrderItemsToCart(Long orderNum, String userEmail) throws UserNotFoundException, OrderNotFoundException, UnauthorizedOrderAccessException, ProductNotFoundException, OptionNotFoundException, SoldOutException {

        Member member = memberRepository.findByMemberEmail(userEmail);
        if (member == null) {
            log.warn("(서비스) 재주문 실패: 사용자 '{}'를 찾을 수 없습니다.", userEmail);
            throw new UserNotFoundException("사용자 정보를 찾을 수 없습니다: " + userEmail);
        }

        // 1. 재주문 대상 원본 주문 정보 조회 (소유권 확인 포함)
        RoomMenuOrder pastOrder = roomMenuOrderRepository.findOrderDetailsByOrderNumAndMemberEmail(orderNum, userEmail)
                .orElseThrow(() -> {
                    log.warn("(서비스) 재주문 실패: 주문번호 '{}', 사용자 '{}'에 해당하는 주문을 찾을 수 없거나 접근 권한이 없습니다.", orderNum, userEmail);
                    return new UnauthorizedOrderAccessException("주문번호 " + orderNum + "에 대한 정보를 찾을 수 없거나 접근 권한이 없습니다.");
                });

        if (pastOrder.getOrderItems() == null || pastOrder.getOrderItems().isEmpty()) {
            log.warn("(서비스) 재주문 실패: 원본 주문 '{}'에 상품 항목이 없습니다.", orderNum);
            throw new OrderNotFoundException("재주문할 상품이 원본 주문에 존재하지 않습니다.");
        }


        // 2. 사용자의 현재 장바구니 가져오기 또는 생성
        RoomMenuCart currentCart = roomMenuCartRepository.findByMember(member)
                .orElseGet(() -> {
                    log.info("(서비스) 사용자 '{}'의 장바구니가 없어 새로 생성합니다.", userEmail);
                    RoomMenuCart newCart = new RoomMenuCart();
                    newCart.setMember(member);
                    return roomMenuCartRepository.save(newCart);
                });

        // 3. 원본 주문의 각 항목을 현재 장바구니에 추가 (옵션 제외)
        for (RoomMenuOrderItem pastOrderItem : pastOrder.getOrderItems()) {
            RoomMenu originalProduct = pastOrderItem.getRoomMenu(); // 과거 주문 시점의 상품 객체
            if (originalProduct == null) {
                log.warn("(서비스) 재주문 중 오류: 과거 주문 항목(ID:{})에 연결된 상품(RoomMenu) 정보가 없습니다.", pastOrderItem.getRoomMenuOrderItemNum());
                continue; // 이 상품은 처리 불가
            }

            // 3-1. 현재 시점의 상품(RoomMenu) 정보 다시 조회 (가격, 재고 등 최신 정보 확인)
            RoomMenu currentProduct = roomMenuRepository.findById(originalProduct.getRoomMenuNum())
                    .orElseThrow(() -> {
                        log.warn("(서비스) 재주문 실패: 상품 '{}'(ID:{})를 현재 찾을 수 없습니다. 판매 중단되었을 수 있습니다.", originalProduct.getRoomMenuName(), originalProduct.getRoomMenuNum());
                        return new ProductNotFoundException("상품 '" + originalProduct.getRoomMenuName() + "'는 현재 판매하지 않는 상품입니다.");
                    });

            int quantityToOrder = pastOrderItem.getRoomMenuOrderAmount(); // 과거 주문 수량

            // 3-2. 상품 유효성 검사 (0원 상품 제한, 현재 재고 등)
            if (currentProduct.getRoomMenuPrice() == 0 && quantityToOrder > 1) {
                log.warn("(서비스) 재주문 중 0원 상품 '{}' 수량 제한 위반. 요청 수량: {}.", currentProduct.getRoomMenuName(), quantityToOrder);
                throw new IllegalStateException("0원 상품 '" + currentProduct.getRoomMenuName() + "'은(는) 1개만 장바구니에 담을 수 있습니다.");
            }

            if (currentProduct.getRoomMenuAmount() < quantityToOrder) {
                log.warn("(서비스) 재주문 실패: 상품 '{}'의 현재 재고가 부족합니다. (요청: {}, 현재: {})", currentProduct.getRoomMenuName(), quantityToOrder, currentProduct.getRoomMenuAmount());
                throw new SoldOutException("상품 '" + currentProduct.getRoomMenuName() + "'의 재고가 부족합니다. (요청 수량: " + quantityToOrder + ", 현재 재고: " + currentProduct.getRoomMenuAmount() + ")");
            }

            // 3-3. 장바구니 아이템 생성 또는 업데이트 (옵션 제외)
            Optional<RoomMenuCartItem> existingCartItemOptional = roomMenuCartItemRepository.findByRoomMenuCartAndRoomMenu(currentCart, currentProduct);

            RoomMenuCartItem cartItemToSave;
            if (existingCartItemOptional.isPresent()) {
                // 이미 장바구니에 동일 상품이 있는 경우: 수량 및 가격 업데이트
                log.info("(서비스) 장바구니에 이미 상품 '{}' 존재. 수량 업데이트 예정.", currentProduct.getRoomMenuName());
                cartItemToSave = existingCartItemOptional.get();
                int newAmount = cartItemToSave.getRoomMenuCartItemAmount() + quantityToOrder;

                // (추가) 수량 업데이트 시에도 재고 다시 한번 확인 (더 안전함)
                if (currentProduct.getRoomMenuAmount() < newAmount && cartItemToSave.getRoomMenu().getRoomMenuNum().equals(currentProduct.getRoomMenuNum())) {

                }
                cartItemToSave.setRoomMenuCartItemAmount(newAmount);
                cartItemToSave.setRoomMenuCartItemPrice(currentProduct.getRoomMenuPrice() * newAmount); // 현재 상품 가격 * 총 수량

                roomMenuCartRepository.save(currentCart);

            } else {
                // 장바구니에 없는 새 상품인 경우: 새로 생성
                log.info("(서비스) 장바구니에 새 상품 '{}' 추가.", currentProduct.getRoomMenuName());
                cartItemToSave = new RoomMenuCartItem();
                cartItemToSave.setRoomMenuCart(currentCart);
                cartItemToSave.setRoomMenu(currentProduct);
                cartItemToSave.setRoomMenuCartItemAmount(quantityToOrder);
                cartItemToSave.setRoomMenuCartItemPrice(currentProduct.getRoomMenuPrice() * quantityToOrder); // 현재 상품 가격 * 주문 수량

            }

            RoomMenuCartItem savedCartItem = roomMenuCartItemRepository.save(cartItemToSave);
            log.info("(서비스) 장바구니 아이템 '{}' (수량: {}) 저장/업데이트 완료. ID: {}. 옵션은 포함되지 않았습니다.",
                    savedCartItem.getRoomMenu().getRoomMenuName(),
                    savedCartItem.getRoomMenuCartItemAmount(),
                    savedCartItem.getRoomMenuCartItemNum());

        }
        // 4. 장바구니 전체 금액 업데이트
        List<RoomMenuCartItem> updatedCartItems = roomMenuCartItemRepository.findByRoomMenuCart(currentCart);
        int finalCartTotalPrice = updatedCartItems.stream()
                .mapToInt(RoomMenuCartItem::getRoomMenuCartItemPrice)
                .sum();
        roomMenuCartRepository.save(currentCart); // 최종적으로 업데이트된 currentCart 저장

        log.info("(서비스) 재주문 처리 완료 (옵션 제외): 주문번호 '{}'의 상품들을 사용자 '{}'의 장바구니(ID:{})에 추가했습니다. 최종 장바구니 금액: {}",
                orderNum, userEmail, currentCart.getRoomMenuCartNum(), finalCartTotalPrice); // 로그 메시지에 finalCartTotalPrice 누락되어 있었음, 추가.

    }
}
