/***********************************************
 * 클래스명 : OrderstoreServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Orderstore;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.OrderstoreRepository;
import com.sixthsense.hexastay.repository.StorecartitemRepository;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.OrderstoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public boolean validOrder(Long orderId, String email) {
        Member inputMember = memberRepository.findByMemberEmail(email);
        Orderstore orderstore = orderstoreRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member orderMember = orderstore.getMember();
        return inputMember.getMemberEmail().equals(orderMember.getMemberEmail());
    }

    @Override
    public void insert(List<Long> itemIdList, String email) {

    }

    @Override
    public void delete(Long orderId) {

    }

    @Override
    public Page<StorecartitemViewDTO> getOrderList(String email, Pageable pageable) {
        return null;
    }
}
