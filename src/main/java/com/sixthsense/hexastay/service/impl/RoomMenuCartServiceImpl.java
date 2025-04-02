package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import com.sixthsense.hexastay.repository.RoomMenuCartRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional

public class RoomMenuCartServiceImpl implements RoomMenuCartService {

    private final RoomMenuCartRepository roomMenuCartRepository;
    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public RoomMenuCartDTO roomMenuCartInsert(RoomMenuCartDTO roomMenuCartDTO) {

        // 장바구니 생성
        RoomMenuCart roomMenuCart = modelMapper.map(roomMenuCartDTO, RoomMenuCart.class);

        // 장바구니 저장
        roomMenuCart = roomMenuCartRepository.save(roomMenuCart);

        // 장바구니에 포함된 항목 처리
        for (RoomMenuCartItemDTO itemDTO : roomMenuCartDTO.getRoomMenuCartItems()) {
            RoomMenuCartItem roomMenuCartItem = new RoomMenuCartItem();
            roomMenuCartItem.setRoomMenuCart(roomMenuCart);
            roomMenuCartItem.setRoomMenu(roomMenuRepository.findById(itemDTO.getRoomMenuNum()).orElseThrow(() -> new RuntimeException("해당 상품이 없습니다.")));
            roomMenuCartItem.setRoomMenuCartItemAmount(itemDTO.getRoomMenuCartItemAmount());
            roomMenuCartItem.setRoomMenuCartItemPrice(itemDTO.getRoomMenuCartItemPrice());

            // RoomMenuCartItem 저장
            roomMenuCart.getRoomMenuCartItems().add(roomMenuCartItem);
        }

        // 장바구니 총 가격 계산
        roomMenuCart.calculateTotalPrice();
        roomMenuCartRepository.save(roomMenuCart);

        // DTO로 변환하여 반환
        return modelMapper.map(roomMenuCart, RoomMenuCartDTO.class);


    }

    @Override
    public RoomMenuCartDTO getCartByMemberId(Long memberNum) {
        RoomMenuCart roomMenuCart = roomMenuCartRepository.findByMember_MemberNum(memberNum);

        return new RoomMenuCartDTO(roomMenuCart);
    }


}
