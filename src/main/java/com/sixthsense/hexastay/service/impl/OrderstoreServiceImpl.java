/***********************************************
 * 클래스명 : OrderstoreServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.OrderstoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class OrderstoreServiceImpl implements OrderstoreService {

    private final OrderstoreRepository orderstoreRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final StorecartitemRepository storecartitemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public boolean validOrder(Long orderId, String email) {
        Member inputMember = memberRepository.findByMemberEmail(email);
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member orderMember = orderstore.getRoom().getMember();
        return inputMember.getMemberEmail().equals(orderMember.getMemberEmail());
    }

    /*예외처리 orelse로 구분... 1:정상 2:room못찾음 3:cartItem못찾음*/
    @Override
    public int insert(List<Long> itemIdList, String email) {
        Member member = memberRepository.findByMemberEmail(email);
        //추가 : room레포지토리에서 멤버로 가장 최근의 room을 찾아 setRoom() 때린다.
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
        Room room = roomRepository.findByMember_MemberNum(member.getMemberNum(), pageable)
                .stream().findFirst().orElse(null);
        if(room==null){return 2;}
        Orderstore order = new Orderstore();
        order.setRoom(room);
        order.setOrderstoreStatus("alive");

        List<Orderstoreitem> itemlist = new ArrayList<>();
        for (Long itemid : itemIdList){
            Storecartitem cartItem = storecartitemRepository.findById(itemid).orElse(null);
            if(cartItem==null){return 3;}
            Storemenu menu = cartItem.getStoremenu();
            Orderstoreitem orderItem = new Orderstoreitem();
            orderItem.setOrderstore(order);
            orderItem.setStoremenu(menu);
            orderItem.setOrderstoreitemAmount(cartItem.getStorecartitemCount());
            orderItem.setOrderstoreitemPrice(cartItem.getStoremenu().getStoremenuPrice());
            orderItem.setOrderstoreitemTotalPrice(cartItem.getStorecartitemCount()*cartItem.getStoremenu().getStoremenuPrice());
            storecartitemRepository.delete(cartItem);
            itemlist.add(orderItem);
        }
        order.setOrderstoreitemList(itemlist);
        orderstoreRepository.save(order);
        return 1;
    }
    /*이거 사용함
    * hotelRoomNum*/
    @Override
    public int insert(List<Long> itemIdList, Long hotelRoomNum) {
        //추가 : room레포지토리에서 방번호로 가장 최근의 room(예약정보)을 찾아 setRoom() 때린다.
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
        Room room = roomRepository.findByHotelRoom_HotelRoomNum(hotelRoomNum, pageable)
                .stream().findFirst().orElse(null);
        if(room==null){return 2;}
        Orderstore order = new Orderstore();
        order.setRoom(room);
        order.setOrderstoreStatus("unpaid");

        if(itemIdList==null||itemIdList.isEmpty()){return 3;}

        List<Orderstoreitem> itemlist = new ArrayList<>();
        for (Long itemid : itemIdList){
            Storecartitem cartItem = storecartitemRepository.findById(itemid).orElse(null);
            if(cartItem==null){return 3;}
            Storemenu menu = cartItem.getStoremenu();
            Orderstoreitem orderItem = new Orderstoreitem();
            orderItem.setOrderstore(order);
            orderItem.setStoremenu(menu);
            orderItem.setOrderstoreitemAmount(cartItem.getStorecartitemCount());
            orderItem.setOrderstoreitemPrice(cartItem.getStoremenu().getStoremenuPrice());
            orderItem.setOrderstoreitemTotalPrice(cartItem.getStorecartitemCount()*cartItem.getStoremenu().getStoremenuPrice());
            storecartitemRepository.delete(cartItem);
            itemlist.add(orderItem);
        }
        order.setOrderstoreitemList(itemlist);
        order.setStore(itemlist.stream().findFirst().orElseThrow().getStoremenu().getStore());
        orderstoreRepository.save(order);
        return 1;
    }

    @Override
    public void paid(Long orderId) {
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderstore.setOrderstoreStatus("paid");
    }

    @Override
    public Long getLastOrder(Long hotelRoom) {
        List<Orderstore> list = orderstoreRepository.findByRoom_HotelRoom_HotelRoomNum(hotelRoom);
        if(list.isEmpty()){
            return null;
        }
        Long orderId = list.getLast().getOrderstoreNum();
        return orderId;
    }

    @Override
    public void cancel(Long orderId) {
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderstore.setOrderstoreStatus("cancel");
    }

    @Override
    public void end(Long orderId) {
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderstore.setOrderstoreStatus("end");
    }


    /* 고객한테 보여줄때는 페이지로 보여주면 되지~~롱*/
    @Override
    public List<OrderstoreViewDTO> getOrderList(String email) {
        List<Orderstore> orderlist = orderstoreRepository.findByRoom_Member_MemberEmail(email);
        List<OrderstoreViewDTO> viewOrderList = new ArrayList<>();
        for (Orderstore orderstore : orderlist){
            OrderstoreViewDTO orderstoreViewDTO = new OrderstoreViewDTO(orderstore);
            List<Orderstoreitem> itemlist = orderstore.getOrderstoreitemList();
            itemlist.forEach(data->{
                OrderstoreitemDTO dto = modelMapper.map(data,OrderstoreitemDTO.class);
                orderstoreViewDTO.addOrderstoreitemDTOList(dto);
            });
            viewOrderList.add(orderstoreViewDTO);
        }
        return viewOrderList;
    }

    @Override
    public List<OrderstoreViewDTO> getOrderList(Long hotelRoomNum) {
        List<Orderstore> orderlist = orderstoreRepository.findByRoom_HotelRoom_HotelRoomNum(hotelRoomNum);
        List<OrderstoreViewDTO> viewOrderList = new ArrayList<>();
        for (Orderstore orderstore : orderlist){
            OrderstoreViewDTO orderstoreViewDTO = new OrderstoreViewDTO(orderstore);
            List<Orderstoreitem> itemlist = orderstore.getOrderstoreitemList();
            itemlist.forEach(data->{
                OrderstoreitemDTO dto = modelMapper.map(data,OrderstoreitemDTO.class);
                orderstoreViewDTO.addOrderstoreitemDTOList(dto);
            });
            viewOrderList.add(orderstoreViewDTO);
        }
        return viewOrderList;
    }

    @Override
    public Page<OrderstoreViewDTO> getOrderList(Long hotelRoomNum, Pageable pageable) {
        Page<Orderstore> orderlist = orderstoreRepository.findByRoom_HotelRoom_HotelRoomNum(hotelRoomNum, pageable);
        Page<OrderstoreViewDTO> viewOrderPage = orderlist.map(orderstore->{
            OrderstoreViewDTO orderstoreViewDTO = new OrderstoreViewDTO(orderstore);
            List<Orderstoreitem> itemlist = orderstore.getOrderstoreitemList();
            itemlist.forEach(item->{
                OrderstoreitemDTO dto = modelMapper.map(item,OrderstoreitemDTO.class);
                orderstoreViewDTO.addOrderstoreitemDTOList(dto);
            });
            return orderstoreViewDTO;
        });
        return viewOrderPage;
    }

    //완료된 주문들만 가져오기
    @Override
    public List<OrderstoreDTO> getAllList() {
        List<Orderstore> orderstoreList = orderstoreRepository.findAll();
        List<OrderstoreDTO> list = orderstoreList.stream().map(data -> {
            OrderstoreDTO dto = modelMapper.map(data, OrderstoreDTO.class);
            dto.setOrderstoreitemDTOList(
                    data.getOrderstoreitemList().stream().map(a->modelMapper.map(a,OrderstoreitemDTO.class)).toList()
            );
            return dto;
        }).toList();
        return list;
    }

    //가게에서 확인하는 들어온 주문 리스트 확인
    @Override
    public List<OrderstoreDTO> getOrderedList(Long storeNum) {
        List<Orderstore> list = orderstoreRepository.findByStoreNum(storeNum);
//        list.forEach(log::info);//됨
        List<OrderstoreDTO> result = list.stream().map(data -> {
            OrderstoreDTO dto = modelMapper.map(data, OrderstoreDTO.class);
            dto.setOrderstoreitemDTOList(
                    data.getOrderstoreitemList().stream().map(
                            a->{
                                OrderstoreitemDTO itemDTO = modelMapper.map(a,OrderstoreitemDTO.class);
                                itemDTO.setStoremenuDTO(modelMapper.map(a.getStoremenu(),StoremenuDTO.class));
                                return itemDTO;
                            }).toList()
            );
            return dto;
        }).toList();
        log.info("서비스에서 찾음? "+result.size());
//        result.forEach(log::info);
        return result;
    }
}