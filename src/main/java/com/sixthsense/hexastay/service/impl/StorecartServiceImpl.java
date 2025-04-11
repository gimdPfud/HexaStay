/***********************************************
 * 클래스명 : StorecartServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StorecartitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Storecart;
import com.sixthsense.hexastay.entity.Storecartitem;
import com.sixthsense.hexastay.entity.Storemenu;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.StorecartRepository;
import com.sixthsense.hexastay.repository.StorecartitemRepository;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.StorecartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
@Log4j2
@Transactional
public class StorecartServiceImpl implements StorecartService {
    private final StoremenuRepository storemenuRepository;
    private final MemberRepository memberRepository;
    private final StorecartRepository storecartRepository;
    private final StorecartitemRepository storecartitemRepository;

    @Override
    public Long addCart(StorecartitemDTO dto, String email) {
        log.info(dto);
        log.info(dto.getStorecartitemCount());
        Storemenu storemenu = storemenuRepository.findById(dto.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByMemberEmail(email);
        Storecart storecart = storecartRepository.findByMember_MemberEmail(email);
        log.info("메뉴, 멤버, 카트 찾음.");
        log.info(storemenu);
        log.info(member);
        log.info(storecart);
        if(storecart==null){
            Storecart newcart = new Storecart();
            newcart.setMember(member);
            storecart = storecartRepository.save(newcart);
            log.info("카트 없어서 새로 만들기");
            log.info(storecart);
        }
        Storecartitem storecartitem
                = storecartitemRepository
                .findByStorecart_StorecartNumAndStoremenu_StoremenuNum(
                        storecart.getStorecartNum(), storemenu.getStoremenuNum()
                );
        log.info(storecartitem);
        if(storecartitem==null){
            log.info("카트아이템 없어서 새로 만들어야함");
            Storecartitem newitem = new Storecartitem();
            newitem.setStorecart(storecart);
            newitem.setStoremenu(storemenu);
            log.info(newitem.getStorecart());
            log.info(newitem.getStoremenu());
            newitem.setStorecartitemCount(dto.getStorecartitemCount());//여기서 문제 발생?
            log.info(dto.getStorecartitemCount());
            log.info(newitem.getStorecartitemCount());
            storecartitem = storecartitemRepository.save(newitem);
            log.info(storecartitem.getStorecartitemCount());
        } else {
            log.info("카트아이템 이미 있음");
            storecartitem.setStorecartitemCount(storecartitem.getStorecartitemCount() + dto.getStorecartitemCount());
        }
        return storecartitem.getStorecartitemNum();
    }

    @Override
    public List<StorecartitemViewDTO> getCartList(String email) {
        List<StorecartitemViewDTO> list = storecartitemRepository.storeCartViewList(email);
        return list;
    }

    @Override
    public boolean validCartItemOwner(Long storeCartItemId, String email) {
        Member inputMember = memberRepository.findByMemberEmail(email);
        Storecartitem itemEntity = storecartitemRepository.findById(storeCartItemId).orElseThrow(EntityNotFoundException::new);
        Member cartMember = itemEntity.getStorecart().getMember();
        return inputMember == cartMember;
    }

    @Override
    public Integer updateCount(Long storeCartItemId, Integer count) {
        Storecartitem itemEntity = storecartitemRepository.findById(storeCartItemId).orElseThrow(EntityNotFoundException::new);
        itemEntity.setStorecartitemCount(count);
        return itemEntity.getStorecartitemCount();
    }

    @Override
    public void deleteCartItem(Long storeCartItemId) {
        storecartitemRepository.deleteById(storeCartItemId);
    }
}
