package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.entity.RoomMenuOrderItem;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuCartItemRepository;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2


public class RoomMenuOrderServiceImpl implements RoomMenuOrderService {

    private final RoomMenuOrderRepository roomMenuOrderRepository;
    private final MemberRepository memberRepository;
    private final RoomMenuRepository roomMenuRepository;
    private final RoomMenuCartItemRepository roomMenuCartItemRepository;

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
        if(roomMenu.getRoomMenuAmount() - roomMenuOrderDTO.getRoomMenuOrderAmount() < 0) {
            throw new IllegalArgumentException("요청 수량이 현재 재고보다 많습니다. (현재수량 : " + roomMenu.getRoomMenuAmount() + ")");
        }

        // 주문 수량만큼 재고 감소
        roomMenu.setRoomMenuAmount(roomMenu.getRoomMenuAmount() - roomMenuOrderDTO.getRoomMenuOrderAmount());

        // 주문 객체에 아이템 리스트 설정
        roomMenuOrder.setOrderItems(roomMenuOrderItemList);

        // 주문 정보 저장
        RoomMenuOrder roomMenuOrderA = roomMenuOrderRepository.save(roomMenuOrder);

        // 저장된 주문의 주문 번호 반환
        return roomMenuOrderA.getRoomMenuOrder();
    }
}
