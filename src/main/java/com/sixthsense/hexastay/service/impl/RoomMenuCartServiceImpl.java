package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuCartItemRepository;
import com.sixthsense.hexastay.repository.RoomMenuCartRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuCartServiceImpl implements RoomMenuCartService {


    private final RoomMenuCartRepository roomMenuCartRepository;
    private RoomMenuCartItemRepository roomMenuCartItemRepository;
    private RoomMenuRepository roomMenuRepository;
    private MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private RoomMenuCart createNewCartForMember(Long memberNum) {
        log.info("장바구니 생성");
        Member member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        RoomMenuCart newCart = new RoomMenuCart();
        newCart.setMember(member);
        newCart.setRoomMenuTotalPrice(0); // 초기 가격은 0으로 설정

        return roomMenuCartRepository.save(newCart);
    }


    @Override
    public RoomMenuCartDTO insertRoomMenuCart(Long memberNum, Long roomMenuNum, Integer amount) {
        log.info("장바구니 추가");

        RoomMenuCart roomMenuCart = roomMenuCartRepository.findByMember_MemberNum(memberNum)
                .orElseGet(() -> createNewCartForMember(memberNum)); // 장바구니가 없으면 새로 생성

        // 상품 조회
        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new RuntimeException("넘버를 찾을 수 없습니다."));

        // 기존 항목이 있는지 확인
        Optional<RoomMenuCartItem> existingItemOpt = roomMenuCartItemRepository.findByRoomMenuCartAndRoomMenu(roomMenuCart, roomMenu);

        RoomMenuCartItem roomMenuCartItem;
        if (existingItemOpt.isPresent()) {
            // 기존 항목이 있으면 수량만 추가하고 가격 갱신
            roomMenuCartItem = existingItemOpt.get();
            roomMenuCartItem.setRoomMenuCartItemAmount(roomMenuCartItem.getRoomMenuCartItemAmount() + amount);
            roomMenuCartItem.setRoomMenuCartItemPrice(roomMenuCartItem.getRoomMenuCartItemAmount() * roomMenu.getRoomMenuPrice());
        } else {
            // 새로운 항목이면 새로 생성
            roomMenuCartItem = new RoomMenuCartItem();
            roomMenuCartItem.setRoomMenuCart(roomMenuCart);
            roomMenuCartItem.setRoomMenu(roomMenu);
            roomMenuCartItem.setRoomMenuCartItemAmount(amount);
            roomMenuCartItem.setRoomMenuCartItemPrice(amount * roomMenu.getRoomMenuPrice());
        }

        // 장바구니 항목 저장
        roomMenuCartItemRepository.save(roomMenuCartItem);

        // 장바구니 총 가격 업데이트
        updateCartTotalPrice(roomMenuCart);

        // 장바구니 DTO로 반환
        return modelMapper.map(roomMenuCart, RoomMenuCartDTO.class);
    }

    // 장바구니 총 가격 계산
    private void updateCartTotalPrice(RoomMenuCart roomMenuCart) {
        int totalPrice = roomMenuCartItemRepository.findAllByRoomMenuCart(roomMenuCart).stream()
                .mapToInt(RoomMenuCartItem::getRoomMenuCartItemPrice)
                .sum();

        roomMenuCart.setRoomMenuTotalPrice(totalPrice);
        roomMenuCartRepository.save(roomMenuCart);
    }
}
