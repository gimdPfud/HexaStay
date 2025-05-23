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
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Storecart;
import com.sixthsense.hexastay.entity.Storecartitem;
import com.sixthsense.hexastay.entity.Storemenu;
import com.sixthsense.hexastay.repository.RoomRepository;
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
    private final StorecartRepository storecartRepository;
    private final StorecartitemRepository storecartitemRepository;
    private final RoomRepository roomRepository;

        @Override
    public int addCart(StorecartitemDTO dto, Long roomNum) {
//        log.info(dto.toString());
//        log.info(dto.getOptionPrice());
//        log.info(dto.getStorecartitemCount());

        //1. 메뉴 조회
        Storemenu storemenu = storemenuRepository.findById(dto.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        //2. room 조회. (왜냐면?? room과 일대일 관계를 맺도록 바꿈. > room은 1번의 숙박/고객/예약 이니까...)
        Room room = roomRepository.findById(roomNum).orElse(null);
        if(room==null){return 1;}

        //3. 장바구니 조회
        Storecart storecart = storecartRepository.findByRoom_RoomNum(roomNum);
//        log.info("메뉴, 룸(중간), 카트 찾음.");
//        log.info(storemenu);
//        log.info(room);
//        log.info(storecart);
        //4. 장바구니가 없으면 하나 만들기.
        if(storecart==null){
            Storecart newcart = new Storecart();
            newcart.setRoom(room);
            storecart = storecartRepository.save(newcart);
//            log.info("카트 없어서 새로 만들기");
//            log.info(storecart);
        }
        //4-2. 장바구니 있음.
        //8. 넣으려는 장바구니아이템의 가게번호를 가져옴.
        Long newItemStoreNum = storemenu.getStore().getStoreNum();
        //9. 장바구니의 아무 장바구니아이템의 가게번호를 가져옴.
        List<Storecartitem> list = storecartitemRepository.findByStorecart_StorecartNum(storecart.getStorecartNum());
        if(list!=null&&!list.isEmpty()){
            Long itemStoreNum = list.getFirst().getStoremenu().getStore().getStoreNum();
            if(!itemStoreNum.equals(newItemStoreNum)){
                return 2;
            }
        }

        //5. 장바구니아이템 조회
            //추가추가 : 옵션 추가추가!!!!!!!!! 옵션까지 같아야 됨.
        Storecartitem storecartitem
                = storecartitemRepository
                .findByStorecart_StorecartNumAndStoremenu_StoremenuNumAndStoremenuOptions(
                        storecart.getStorecartNum(), storemenu.getStoremenuNum(), dto.getStoremenuOptions()
                );
//        log.info(storecartitem);
        //6. 장바구니아이템이 없다면 새로만들기
        if(storecartitem==null){
            Storecartitem newitem = new Storecartitem();
            newitem.setStorecart(storecart);
            newitem.setStoremenu(storemenu);
            newitem.setStorecartitemCount(dto.getStorecartitemCount());
            newitem.setStoremenuOptions(dto.getStoremenuOptions());
            newitem.setOptionPrice(dto.getOptionPrice());
            storecartitem = storecartitemRepository.save(newitem);
        }
        //7. 장바구니아이템이 있음, 개수 조정
        else {
//            log.info("카트아이템 이미 있음");
            int newCount = storecartitem.getStorecartitemCount() + dto.getStorecartitemCount();
            if (newCount > 99){
                return 3;
            }else {
                storecartitem.setStorecartitemCount(newCount);
            }
        }
        log.info("리턴 전 확인 : "+storecartitem.getStorecartitemNum());
        return 4;
    }

        @Override
    public List<StorecartitemViewDTO> getCartList(Long roomNum) {
        List<StorecartitemViewDTO> list = storecartitemRepository.storeCartViewList(roomNum);
        return list;
    }

    @Override
    public Long getItemCount(Long roomNum) {
        return storecartitemRepository.countByStorecart_Room(roomNum);
    }

    @Override
    public boolean validCartItemOwner(Long storeCartItemId, Long roomNum) {
        Room inputRoom = roomRepository.findById(roomNum).orElse(null);
        Storecartitem itemEntity = storecartitemRepository.findById(storeCartItemId).orElseThrow(EntityNotFoundException::new);
        Room cartRoom = itemEntity.getStorecart().getRoom();
        return inputRoom == cartRoom;
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
    public void clearCartItems(Long roomNum) {
        Storecart cart = storecartRepository.findByRoom_RoomNum(roomNum);
        List<Storecartitem> list = storecartitemRepository.findByStorecart_StorecartNum(cart.getStorecartNum());
        storecartitemRepository.deleteAll(list);
    }
}