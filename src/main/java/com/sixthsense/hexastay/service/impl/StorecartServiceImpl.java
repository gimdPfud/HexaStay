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

        //1. 메뉴 조회
        Storemenu storemenu = storemenuRepository.findById(dto.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        //2. 멤버 조회
        Member member = memberRepository.findByMemberEmail(email);
        //3. 장바구니 조회
        Storecart storecart = storecartRepository.findByMember_MemberEmail(email);
        log.info("메뉴, 멤버, 카트 찾음.");
        log.info(storemenu);
        log.info(member);
        log.info(storecart);
        //4. 장바구니가 없으면 하나 만들기.
        if(storecart==null){
            Storecart newcart = new Storecart();
            newcart.setMember(member);
            storecart = storecartRepository.save(newcart);
            log.info("카트 없어서 새로 만들기");
            log.info(storecart);
        }
        //4-2. 장바구니 있음.
        //8. 넣으려는 장바구니아이템의 가게번호를 가져옴.
        Long newItemStoreNum = storemenu.getStore().getStoreNum();
        //9. 장바구니의 아무 장바구니아이템의 가게번호를 가져옴.
        List<Storecartitem> list = storecartitemRepository.findByStorecart_StorecartNum(storecart.getStorecartNum());
        if(list!=null&&!list.isEmpty()){
            Long itemStoreNum = list.getFirst().getStoremenu().getStore().getStoreNum();
            if(!itemStoreNum.equals(newItemStoreNum)){
                return null;
            }
        }

        //5. 장바구니아이템 조회
        Storecartitem storecartitem
                = storecartitemRepository
                .findByStorecart_StorecartNumAndStoremenu_StoremenuNum(
                        storecart.getStorecartNum(), storemenu.getStoremenuNum()
                );
        log.info(storecartitem);
        //6. 장바구니아이템이 없다면 새로만들기
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
        }
        //7. 장바구니아이템이 있음, 개수 조정
        else {
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

    @Override
    public void clearCartItems(String email) {
        Storecart cart = storecartRepository.findByMember_MemberEmail(email);
        List<Storecartitem> list = storecartitemRepository.findByStorecart_StorecartNum(cart.getStorecartNum());
        storecartitemRepository.deleteAll(list);
    }
}
