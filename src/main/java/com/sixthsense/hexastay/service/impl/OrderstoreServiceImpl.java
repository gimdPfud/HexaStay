/***********************************************
 * 클래스명 : OrderstoreServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.OrderstoreViewDTO;
import com.sixthsense.hexastay.dto.OrderstoreitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.OrderstoreRepository;
import com.sixthsense.hexastay.repository.StorecartitemRepository;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.OrderstoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class OrderstoreServiceImpl implements OrderstoreService {

    private final OrderstoreRepository orderstoreRepository;
    private final MemberRepository memberRepository;
    private final StoremenuRepository storemenuRepository;
    private final StorecartitemRepository storecartitemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public boolean validOrder(Long orderId, String email) {
        Member inputMember = memberRepository.findByMemberEmail(email);
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member orderMember = orderstore.getMember();
        return inputMember.getMemberEmail().equals(orderMember.getMemberEmail());
    }

    @Override
    public void insert(List<Long> itemIdList, String email) {
        Member member = memberRepository.findByMemberEmail(email);
        Orderstore order = new Orderstore();
        order.setMember(member);
        order.setOrderstoreStatus("alive");

        List<Orderstoreitem> itemlist = new ArrayList<>();
        for (Long itemid : itemIdList){
            Storecartitem cartItem = storecartitemRepository.findById(itemid).orElseThrow(EntityNotFoundException::new);
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
    }

    @Override
    public void delete(Long orderId) {
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderstore.setOrderstoreStatus("cancel");
    }


    /* 고객한테 보여줄때는 페이지로 보여주면 되지~~롱*/
    @Override
    public Page<OrderstoreViewDTO> getOrderList(String email, Pageable pageable) {
        Page<Orderstore> orderlist = orderstoreRepository.findByMember_MemberEmail(email,pageable);
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
        return new PageImpl<>(viewOrderList, pageable, orderlist.getTotalElements());
    }
}
