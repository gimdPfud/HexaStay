/***********************************************
 * 클래스명 : StorecartServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.StorecartitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.StorecartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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

    /*메소드 : principal으로 hotelroomNum반환하는 메소드 */
    private final MemberRepository memberRepository;
    private final RoomServiceImpl roomService;
    @Override
    public Long principalToHotelroomNum(Principal principal){
        String email = principal.getName();
        Long memberNum = memberRepository.findByMemberEmail(email).getMemberNum();
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
        HotelRoomDTO hotelRoomDTO = roomService.getHotelRoomsByMember(memberNum,pageable).stream().findFirst().orElseThrow(EntityNotFoundException::new);
        return hotelRoomDTO.getHotelRoomNum();
    }

        @Override
    public Long addCart(StorecartitemDTO dto, Long hotelroomNum) {
//        log.info(dto);
//        log.info(dto.getStorecartitemCount());

        //1. 메뉴 조회
        Storemenu storemenu = storemenuRepository.findById(dto.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        //2. room 조회. (왜냐면?? room과 일대일 관계를 맺도록 바꿈. > room은 1번의 숙박/고객/예약 이니까...)
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
        Room room = roomRepository.findByHotelRoom_HotelRoomNum(hotelroomNum,pageable).stream().findFirst().orElse(null);
        if(room==null){return null;}//room 못찾으면 1 반환

        //3. 장바구니 조회
        Storecart storecart = storecartRepository.findByRoom_HotelRoom_HotelRoomNum(hotelroomNum);
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
                return null;
            }
        }

        //5. 장바구니아이템 조회
        Storecartitem storecartitem
                = storecartitemRepository
                .findByStorecart_StorecartNumAndStoremenu_StoremenuNum(
                        storecart.getStorecartNum(), storemenu.getStoremenuNum()
                );
//        log.info(storecartitem);
        //6. 장바구니아이템이 없다면 새로만들기
        if(storecartitem==null){
            Storecartitem newitem = new Storecartitem();
            newitem.setStorecart(storecart);
            newitem.setStoremenu(storemenu);
            newitem.setStorecartitemCount(dto.getStorecartitemCount());//여기서 문제 발생?
            storecartitem = storecartitemRepository.save(newitem);
        }
        //7. 장바구니아이템이 있음, 개수 조정
        else {
//            log.info("카트아이템 이미 있음");
            storecartitem.setStorecartitemCount(storecartitem.getStorecartitemCount() + dto.getStorecartitemCount());
        }
        return storecartitem.getStorecartitemNum();
    }

        @Override
    public List<StorecartitemViewDTO> getCartList(Long hotelRoomNum) {
        List<StorecartitemViewDTO> list = storecartitemRepository.storeCartViewList(hotelRoomNum);
        return list;
    }

        @Override
    public boolean validCartItemOwner(Long storeCartItemId, Long hotelroomNum) {
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"roomNum"));
        Room inputRoom = roomRepository.findByHotelRoom_HotelRoomNum(hotelroomNum,pageable).stream().findFirst().orElse(null);
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
    public void clearCartItems(Long hotelroomNum) {
        Storecart cart = storecartRepository.findByRoom_HotelRoom_HotelRoomNum(hotelroomNum);
        List<Storecartitem> list = storecartitemRepository.findByStorecart_StorecartNum(cart.getStorecartNum());
        storecartitemRepository.deleteAll(list);
    }
}