package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuCartServiceImpl implements RoomMenuCartService {


    private final RoomMenuCartRepository roomMenuCartRepository;
    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final RoomMenuCartItemRepository roomMenuCartItemRepository;
    private final MemberRepository memberRepository;
    private final RoomMenuTranslationRepository roomMenuTranslationRepository;
    private final CouponRepository couponRepository;
    private final RoomMenuOptionRepository roomMenuOptionRepository;
    private final RoomMenuService roomMenuService;
    private final RoomMenuCartItemOptionRepository roomMenuCartItemOptionRepository;


    /***************************************************
     *
     * 메소드명   : RoomMenuCartRead
     * 기능      : 이메일을 사용하여 룸서비스 메뉴 정보를 조회하고, 해당 정보를 DTO로 변환하여 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     * 상세 설명 : 이 메소드는 사용자의 이메일을 기반으로 해당 사용자의 룸서비스 메뉴 정보를 조회
     *
     ****************************************************/
    @Override
    public RoomMenuDTO RoomMenuCartRead(String email) {
        RoomMenu roomMenu =
                roomMenuRepository.findByRoom_Member_MemberEmail(email);

        log.info(email);

        RoomMenuDTO roomMenuDTO =
                modelMapper.map(roomMenu, RoomMenuDTO.class);

        return roomMenuDTO;
    }

    /***************************************************
     *
     * 메소드명   : RoomMenuCartInsert
     * 기능      : 룸서비스 장바구니에 아이템을 추가하거나 기존 아이템의 수량을 업데이트하는 서비스 메소드
     *            - 장바구니가 존재하지 않으면 새로 생성
     *            - 아이템이 이미 장바구니에 있으면 수량을 추가
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public Long RoomMenuCartInsert(String email, RoomMenuCartItemDTO roomMenuCartItemDTO) {
        log.info("장바구니 추가 시스템 도입, 회원 이메일: {}", email);

        // 룸서비스 메뉴에서 해당된 ID값을 찾아 해당된 회원을 설정하기.
        RoomMenu roomMenu =
                roomMenuRepository.findById(roomMenuCartItemDTO.getRoomMenuCartItemNum())
                        .orElseThrow(EntityNotFoundException::new);
        log.info("roomMenuCartItemDTO.getRoomMenuCartItemNum() = {}", roomMenuCartItemDTO.getRoomMenuCartItemNum());
        log.info("누가 산 장바구니?" + email);

        // 누가 샀는가?
        Member member = memberRepository.findByMemberEmail(email);
        log.info("멤버 찾음" + member);


        // 내 장바구니 구현
        RoomMenuCart roomMenuCart =
                roomMenuCartRepository.findByMember_MemberEmail(email);



        if (roomMenuCart == null) {
            log.info("회원의 장바구니가 존재하지 않습니다.");
            RoomMenuCart insertCart = new RoomMenuCart();
            insertCart.setMember(member);
            roomMenuCart =
                    roomMenuCartRepository.save(insertCart);     // 장바구니가 없다면 새로 만들기
        }
        //장바구니에 담겨진 아이템 찾기
        RoomMenuCartItem roomMenuCartItem =
                roomMenuCartItemRepository
                        .findByRoomMenuCartAndRoomMenu(roomMenuCart, roomMenu)
                        .orElse(null);

        if (roomMenuCartItem == null) {
            //장바구니에 아이템이 담겨있지 않다면
            RoomMenuCartItem insertCartItem = new RoomMenuCartItem();
            insertCartItem.setRoomMenuCart(roomMenuCart); // 장바구니
            // todo : (2) 여기 이상할지도 모름
            // fixme : 레포지토리에 추가 안될지도 모름.
            log.info(" RoomMenuCart 연결됨 - cartId: {}", roomMenuCart.getRoomMenuCartNum());
            insertCartItem.setRoomMenu(roomMenu); // 장바구니에 담길 아이템
            log.info(" 아이템메뉴 설정됨 - pk : roommenunum: {}, roommenu: {}", roomMenu.getRoomMenuNum(), roomMenu.getRoomMenuName());
            insertCartItem.setRoomMenuCartItemAmount(roomMenuCartItemDTO.getRoomMenuCartItemAmount()); // 수량
            log.info(" 수량 설정됨 - amount: {}", roomMenuCartItemDTO.getRoomMenuCartItemAmount());
            insertCartItem.setRoomMenuCartItemCount(roomMenuCartItemDTO.getRoomMenuCartItemAmount());
            log.info("총 수량 설정됨 - Count {}", roomMenuCartItemDTO.getRoomMenuCartItemCount());

            int finalPrice = roomMenu.getRoomMenuPrice();
            int optionTotalPrice = 0; // 옵션 가격 총합

            List<RoomMenuCartItemOptionDTO> optionDTOList = roomMenuCartItemDTO.getSelectedOptions();

            if (optionDTOList != null && !optionDTOList.isEmpty()) {
                for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                    RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRooMenuCartItemOptionNum())
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다."));

                    RoomMenuCartItemOption option = new RoomMenuCartItemOption();
                    option.setRoomMenuCartItem(insertCartItem); // FK 연결
                    option.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName()); // 이름 복사
                    option.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice()); // 단가
                    option.setRoomMenuCartItemOptionAmount(optionDTO.getRoomMenuCartItemOptionAmount()); // 수량

                    optionTotalPrice += menuOption.getRoomMenuOptionPrice() * optionDTO.getRoomMenuCartItemOptionAmount();

                    roomMenuCartItemOptionRepository.save(option);
                }
            }


            if (roomMenuCartItemDTO.getRoomMenuSelectOptionPrice() != null) {
                finalPrice = (roomMenu.getRoomMenuPrice() + optionTotalPrice) * roomMenuCartItemDTO.getRoomMenuCartItemAmount();
            }
            insertCartItem.setRoomMenuCartItemPrice(finalPrice);
            insertCartItem.setRoomMenuSelectOptionName(roomMenuCartItemDTO.getRoomMenuSelectOptionName());
            insertCartItem.setRoomMenuSelectOptionPrice(roomMenuCartItemDTO.getRoomMenuSelectOptionPrice());


            // 아이템을 저장하자.
            roomMenuCartItem =
                    roomMenuCartItemRepository.save(insertCartItem);

            // 장바구니에 아이템이 추가된 후 로그 출력
            log.info("장바구니에 아이템 추가됨 장바구니에 추가된 pk 넘버: " + roomMenu.getRoomMenuNum());


            if (optionDTOList != null && !optionDTOList.isEmpty()) {
                for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                    // RoomMenuOption을 DB에서 다시 조회!
                    RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRooMenuCartItemOptionNum())
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다."));

                    RoomMenuCartItemOption option = new RoomMenuCartItemOption();
                    option.setRoomMenuCartItem(roomMenuCartItem); // FK 연결
                    option.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName()); // 이름 복사
                    option.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice()); // 가격 복사
                    option.setRoomMenuCartItemOptionAmount(optionDTO.getRoomMenuCartItemOptionAmount()); // 수량은 그대로

                    roomMenuCartItemOptionRepository.save(option);
                }
            }

            // 리턴값 반환
            return roomMenuCartItem.getRoomMenuCartItemNum();

        } else {
            log.info("이미 장바구니에 동일한 아이템이 있습니다.");

            int newAmount = roomMenuCartItem.getRoomMenuCartItemAmount() + roomMenuCartItemDTO.getRoomMenuCartItemAmount();
            roomMenuCartItem.setRoomMenuCartItemAmount(newAmount);

            int newCount = (roomMenuCartItem.getRoomMenuCartItemCount() != null ? roomMenuCartItem.getRoomMenuCartItemCount() : 0)
                    + roomMenuCartItemDTO.getRoomMenuCartItemAmount();
            roomMenuCartItem.setRoomMenuCartItemCount(newCount);

            //  옵션 정보도 업데이트
            roomMenuCartItem.setRoomMenuSelectOptionName(roomMenuCartItemDTO.getRoomMenuSelectOptionName());
            roomMenuCartItem.setRoomMenuSelectOptionPrice(roomMenuCartItemDTO.getRoomMenuSelectOptionPrice());

            //  가격도 다시 계산
            int basePrice = roomMenu.getRoomMenuPrice(); // 기본 가격
            Integer optionPrice = roomMenuCartItemDTO.getRoomMenuSelectOptionPrice(); // 옵션 가격

            if (optionPrice == null) {
                optionPrice = 0;
            }

            int totalPricePerOne = basePrice + optionPrice;
            int finalPrice = totalPricePerOne * newAmount; // newAmount는 최종 수량

            roomMenuCartItem.setRoomMenuCartItemPrice(finalPrice);

            return roomMenuCartItem.getRoomMenuCartItemNum();
        }
    }

    /***************************************************
     *
     * 메소드명   : RoomMenuCartItemList
     * 기능      : 룸서비스 장바구니에서 특정 회원의 아이템 목록을 조회하고, 해당 아이템이
     *            특정 회원의 카트에 속하는지 검증하는 서비스 메소드
     *            - 특정 카트 아이템을 카트 소유자와 비교하여, 해당 아이템이 로그인한 회원의 카트에 속하는지 확인
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public Page<RoomMenuCartDetailDTO> RoomMenuCartItemList(String email, Pageable pageable) {
        log.info("특정 회원 아이템 목록 조회 서비스 진입" + email);

        Page<RoomMenuCartDetailDTO> roomMenuCartDetailDTOPage =
                roomMenuCartItemRepository.findByCartDetailDTOList(email, pageable);


        roomMenuCartDetailDTOPage.forEach(dto -> {
            List<RoomMenuCartItemOption> optionList =
                    roomMenuCartItemOptionRepository.findByRoomMenuCartItem_RoomMenuCartItemNum(dto.getRoomMenuCartDetailNum());

            List<RoomMenuCartItemOptionDTO> optionDTOs = optionList.stream()
                    .map(opt -> new RoomMenuCartItemOptionDTO(
                            opt.getRoomMenuCartItemOptionName(),
                            opt.getRoomMenuCartItemOptionPrice(),
                            opt.getRoomMenuCartItemOptionAmount()
                    )).collect(Collectors.toList());

            dto.setOptionList(optionDTOs); // 옵션 리스트 주입

            log.info("옵션 DTO 리스트: {}", optionDTOs);
        });


        return roomMenuCartDetailDTOPage;
    }


    /***************************************************
     *
     * 클래스명   : verificationRoomMenuCartItem
     * 기능      : 룸서비스 장바구니에서 특정 회원의 아이템이 해당 회원의 카트에 속하는지 확인하는 서비스 메소드
     *            - 주어진 이메일을 기준으로 로그인한 회원 정보를 조회
     *            - 카트 아이템 번호를 기준으로 아이템을 조회하고, 해당 아이템이 로그인한 회원의 카트에 속하는지 검증
     *            - 동일한 회원의 카트에 속하면 true를, 아니면 false를 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public boolean verificationRoomMenuCartItem(Long RoomMenuCartItemNum, String email) {
        // 특정 회원의 아이템 목록을 조회하는 서비스가 진입한 것을 로그로 출력
        log.info("특정 회원 아이템 목록 조회 서비스 진입: {}", email);

        // 주어진 이메일을 사용하여 현재 로그인한 회원의 정보를 조회
        Member member = memberRepository.findByMemberEmail(email);

        // 카트 아이템 번호를 기준으로 카트 아이템을 조회
        Optional<RoomMenuCartItem> roomMenuCartItem =
                roomMenuCartItemRepository.findById(RoomMenuCartItemNum);

        // 카트 아이템이 존재하지 않으면 예외를 던짐
        RoomMenuCartItem menuCartItem =
                roomMenuCartItem.orElseThrow(EntityNotFoundException::new);

        // 카트 아이템이 속한 카트를 조회
        RoomMenuCart roomMenuCart = menuCartItem.getRoomMenuCart();

        // 카트에 연결된 회원 정보 조회
        Member cartMember = roomMenuCart.getMember();

        // 로그인한 사용자와 카트 소유자가 동일한지 비교
        if (member.getMemberNum() == cartMember.getMemberNum()) {
            // 동일한 회원일 경우 true 반환
            log.info("로그인한 회원과 카트 소유자가 동일함: {}", member.getMemberEmail());
            return true;
        } else {
            // 다른 회원일 경우 false 반환
            log.info("로그인한 회원과 카트 소유자가 다름: {} vs {}", member.getMemberEmail(), cartMember.getMemberEmail());
            return false;
        }
    }

    /***************************************************
     *
     * 메소드명   : updateRoomCartItemCount
     * 기능      : 룸서비스 장바구니 아이템의 수량을 업데이트하는 서비스 메소드
     *            - 특정 장바구니 아이템의 수량을 업데이트
     *            - 장바구니 아이템이 존재하지 않으면 예외를 던짐
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public void RoomMenuCartItemAmountUpdate(Long RoomMenuCartItemNum, Integer roomMenuCartItemAmount) {
        log.info("장바구니 수량 업데이트 서비스 진입");
        log.info("아이템의 pk" + RoomMenuCartItemNum);
        log.info("아이템의 수량" + roomMenuCartItemAmount);
        RoomMenuCartItem roomMenuCartItem =
                roomMenuCartItemRepository.findById(RoomMenuCartItemNum).orElseThrow(EntityNotFoundException::new);

        roomMenuCartItem.setRoomMenuCartItemAmount(roomMenuCartItemAmount);

    }

    /***************************************************
     *
     * 매소드명   : RoomCartMenuCartItemDelete
     * 기능      : 룸서비스 장바구니에서 특정 아이템을 삭제하는 서비스 메소드
     *            - 주어진 아이템 번호를 기준으로 장바구니 아이템을 찾아 삭제 처리
     *            - 삭제 후 해당 아이템을 조회하여 삭제 확인
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : -
     *
     ****************************************************/

    @Override
    public void RoomCartMenuCartItemDelete(Long roomMenuCartItemNum) {
        log.info("서비스로 들어온 pk: {}", roomMenuCartItemNum);

        // 주어진 roomMenuCartItemNum을 기반으로 장바구니 아이템을 조회
        RoomMenuCartItem roomMenuCartItem =
                roomMenuCartItemRepository.findById(roomMenuCartItemNum).orElseThrow(EntityNotFoundException::new);

        // 장바구니 아이템을 삭제
        roomMenuCartItemRepository.delete(roomMenuCartItem);
        log.info("아이템 삭제 완료: {}", roomMenuCartItemNum);

//        // 삭제 후 해당 아이템이 존재하는지 확인 (아이템이 존재하면 예외가 발생)
//        roomMenuCartItemRepository.findById(roomMenuCartItemNum).orElseThrow(() -> {
//            log.info("삭제된 아이템은 더 이상 존재하지 않습니다: {}", roomMenuCartItemNum);
//            return new EntityNotFoundException("아이템이 삭제되었습니다.");
//        }); // todo(7) 코드를 제거했음 오류 생길수도 있음
    }

    /***********************************************
     * 메서드명 : read
     * 기능 : 특정 룸 메뉴 번호를 기반으로 룸 메뉴 상세 정보를 조회
     * 매개변수 : Long num - 룸 메뉴 번호
     * 반환값 : RoomMenuDTO - 조회된 룸 메뉴 DTO
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-09
     * 수정일 : -
     * ***********************************************/

    // 상세보기
    @Override
    public RoomMenuDTO read(Long num, Locale locale,  Model model) {
        log.info("상세보기 Cart 페이지 서비스 진입" + num);

        // DB에서 메뉴 정보를 가져옴
        RoomMenu roomMenu = roomMenuRepository.findByRoomMenuNum(num);
        RoomMenuDTO roomMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);


        // 메뉴에 해당하는 번역 정보 찾기
        Optional<RoomMenuTranslation> translation = roomMenuTranslationRepository
                .findByRoomMenu_RoomMenuNumAndLocale(roomMenu.getRoomMenuNum(), locale.getLanguage());

        // 번역이 있을 경우
        if (translation.isPresent()) {
            roomMenuDTO.setRoomMenuName(translation.get().getRoomMenuTransLationName());
            roomMenuDTO.setRoomMenuContent(translation.get().getRoomMenuTransLationContent());
            // 필요한 다른 번역 필드가 있다면 여기에 추가
        } else {
            log.warn("번역 정보가 없습니다. 기본 언어로 표시됩니다.");
        }

        // 메뉴에 해당하는 옵션들을 가져와서 RoomMenuDTO에 추가
        List<RoomMenuOption> options = roomMenuOptionRepository.findByRoomMenu(roomMenu); // 메뉴에 관련된 옵션들 조회
        List<RoomMenuOptionDTO> optionDTOs = options.stream()
                .map(option -> modelMapper.map(option, RoomMenuOptionDTO.class)) // RoomMenuOption을 DTO로 변환
                .collect(Collectors.toList());

        roomMenuDTO.setOptions(optionDTOs);  // RoomMenuDTO에 옵션 리스트 추가

        model.addAttribute("roomMenuDTO", roomMenuDTO);

        log.info("옵션 목록: {}", roomMenuDTO.getOptions());

        log.info("최종 반환되는 roomMenuDTO: {}", roomMenuDTO);


        return roomMenuDTO;  // 최종적으로 옵션을 포함한 DTO 반환
    }

    /***********************************************
     * 메서드명 : getTotalCartItemCount
     * 기능 : 특정 회원의 이메일을 기반으로 장바구니에 담긴 총 아이템 수를 조회.
     * 매개변수 : String memberEmail - 회원 이메일
     * 반환값 : Integer - 총 장바구니 아이템 수 (null일 경우 0 반환)
     * 작성자 : 자동 생성
     * 작성일 : 2025-04-14
     * 수정일 : -
     * ***********************************************/

    @Override
    public Integer getTotalCartItemCount(String memberEmail) {
        Integer count = roomMenuCartItemRepository.getTotalItemCountByMemberEmail(memberEmail);
        return count != null ? count : 0;
    }

    // 쿠폰 할인
    @Override
    public Integer getTotalPriceWithCoupon(String email, Long couponNum) {
        Member member = memberRepository.findByMemberEmail(email);
        Coupon coupon = couponRepository.findById(couponNum).orElseThrow();

        // 총 금액 계산
        int originalPrice = getCartTotal(member); // 장바구니 금액 계산 메서드

        // 쿠폰 할인 적용
        int discount = (originalPrice * coupon.getDiscountRate()) / 100;

        return originalPrice - discount;
    }

    // 장바구니 조회 후 쿠폰 적용
    @Override
    public Integer getCartTotal(Member member) {
        // 1. 해당 멤버의 장바구니 조회
        RoomMenuCart cart = roomMenuCartRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

        // 2. 장바구니 아이템들 조회
        List<RoomMenuCartItem> items = roomMenuCartItemRepository.findByRoomMenuCart(cart);

        // 3. 가격 × 수량 합산
        int total = 0;
        for (RoomMenuCartItem item : items) {
            int itemPrice = item.getRoomMenu().getRoomMenuPrice();
            int quantity = item.getRoomMenuCartItemAmount();
            total += itemPrice * quantity;
        }

        return total;
    }

}