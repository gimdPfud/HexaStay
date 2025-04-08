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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
        Storemenu storemenu = storemenuRepository.findById(dto.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByMemberEmail(email);
        Storecart storecart = storecartRepository.findByMember_MemberEmail(email);
        if(storecart==null){
            Storecart newcart = new Storecart();
            newcart.setMember(member);
            storecart = storecartRepository.save(newcart);
        }
        Storecartitem storecartitem
                = storecartitemRepository
                .findByStorecart_StorecartNumAndStoremenu_StoremenuNum(
                        storecart.getStorecartNum(), storemenu.getStoremenuNum()
                );
        if(storecartitem==null){
            Storecartitem newitem = new Storecartitem();
            newitem.setStorecart(storecart);
            newitem.setStoremenu(storemenu);
            newitem.setStorecartitemCount(dto.getStorecartitemCount());
            storecartitem = storecartitemRepository.save(newitem);
        } else {
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
    public void updateCount(Long storeCartItemId, Integer count) {
        Storecartitem itemEntity = storecartitemRepository.findById(storeCartItemId).orElseThrow(EntityNotFoundException::new);
        itemEntity.setStorecartitemCount(count);
    }

    @Override
    public void deleteCartItem(Long storeCartItemId) {
        storecartitemRepository.deleteById(storeCartItemId);
    }
}
