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
    private final RoomRepository roomRepository;


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

        // 1. 룸서비스 메뉴에서 해당된 ID값을 찾아 엔티티 가져오기
        RoomMenu roomMenu =
                roomMenuRepository.findById(roomMenuCartItemDTO.getRoomMenuCartItemNum())
                        .orElseThrow(() -> new EntityNotFoundException("해당 룸 메뉴를 찾을 수 없습니다. ID: " + roomMenuCartItemDTO.getRoomMenuCartItemNum()));
        log.info("조회된 RoomMenu: {}", roomMenu.getRoomMenuName());

        // 2. 누가 샀는가? - Member 엔티티 가져오기
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) {
            throw new EntityNotFoundException("해당 이메일의 멤버를 찾을 수 없습니다. Email: " + email);
        }
        log.info("조회된 Member: {}", member.getMemberEmail());

        // ★★★ 3. 이 멤버의 현재 활성화된 Room (투숙) 정보 찾기 ★★★
        // 추가하신 findByMemberAndCheckOutDateIsNull 메서드를 사용합니다.
        Optional<Room> currentRoomOptional = roomRepository.findByMemberAndCheckOutDateIsNull(member);

        // 현재 투숙 중인 Room 정보가 없는 경우 예외 발생
        Room currentRoom = currentRoomOptional.orElseThrow(
                () -> new EntityNotFoundException("현재 투숙 중인 방이 없습니다. 룸서비스 주문은 체크인 후에 가능합니다.")
        );

        log.info("조회된 현재 Room (투숙) 정보. Room PK: {}, HotelRoomNum: {}", currentRoom.getRoomNum(), currentRoom.getHotelRoom().getHotelRoomNum());


        // ★★★ 4. 현재 Room (투숙)과 연결된 RoomMenuCart (장바구니) 찾거나 생성하기 ★★★
        // 장바구니는 이제 Room에 연결됩니다. findByRoom(Room room) 메서드를 사용합니다.
        Optional<RoomMenuCart> roomMenuCartOptional = roomMenuCartRepository.findByRoom(currentRoom);

        RoomMenuCart roomMenuCart;

        if (roomMenuCartOptional.isEmpty()) {
            log.info("현재 Room (투숙 정보)에 연결된 장바구니가 존재하지 않습니다. 새로 생성합니다.");
            RoomMenuCart newCart = new RoomMenuCart();
            newCart.setRoom(currentRoom); // ★ 새로 생성 시, Room 정보 설정 ★
            newCart.setMember(member);   // ★ RoomMenuCart 엔티티에 Member FK도 있으므로, Member 정보도 설정 ★
            roomMenuCart = roomMenuCartRepository.save(newCart);     // 장바구니가 없다면 새로 만들기
            log.info("새 장바구니 생성됨. 장바구니 PK: {}", roomMenuCart.getRoomMenuCartNum());
        } else {
            roomMenuCart = roomMenuCartOptional.get(); // Optional에서 엔티티 가져오기
            log.info("현재 Room (투숙 정보)에 연결된 기존 장바구니 찾음. 장바구니 PK: {}", roomMenuCart.getRoomMenuCartNum());
            // 기존 장바구니를 찾은 경우에도 Member 정보가 올바른지 한 번 더 확인 (findByRoom으로 찾았으므로 보통 일치해야 함)
            if (!roomMenuCart.getMember().getMemberNum().equals(member.getMemberNum())) {
                log.error("찾은 장바구니({}번)의 멤버({})와 현재 멤버({})가 일치하지 않습니다.",
                        roomMenuCart.getRoomMenuCartNum(), roomMenuCart.getMember().getMemberEmail(), member.getMemberEmail());
                throw new IllegalStateException("장바구니 정보가 올바르지 않습니다. (멤버 불일치)");
            }
        }

        // 5. 장바구니에 담겨진 동일 아이템 찾기 (RoomMenuCart와 RoomMenu 기준으로)
        // findByRoomMenuCartAndRoomMenu 메서드는 Optional을 반환하도록 수정하는 것이 더 안전합니다.
        Optional<RoomMenuCartItem> existingItemOptional =
                roomMenuCartItemRepository.findByRoomMenuCartAndRoomMenu(roomMenuCart, roomMenu);


        if (existingItemOptional.isEmpty()) { // Optional.isEmpty() 사용
            // 6-1. 장바구니에 동일 아이템이 담겨있지 않다면 새로 생성
            log.info("장바구니에 신규 아이템 추가: {}", roomMenu.getRoomMenuName());
            RoomMenuCartItem insertCartItem = new RoomMenuCartItem();
            insertCartItem.setRoomMenuCart(roomMenuCart); // ★ 새로 생성 시, 장바구니 연결 ★

            insertCartItem.setRoomMenu(roomMenu); // 장바구니에 담길 아이템
            insertCartItem.setRoomMenuCartItemAmount(roomMenuCartItemDTO.getRoomMenuCartItemAmount()); // 수량
            insertCartItem.setRoomMenuCartItemCount(roomMenuCartItemDTO.getRoomMenuCartItemAmount()); // Count는 보통 Amount와 같게 시작

            // 옵션 및 가격 계산 로직 (기존 코드와 유사)
            int basePrice = roomMenu.getRoomMenuPrice();
            int optionTotalPrice = 0; // 옵션 가격 총합 (계산을 위한 합계)

            List<RoomMenuCartItemOptionDTO> optionDTOList = roomMenuCartItemDTO.getSelectedOptions();

            // 옵션 가격 먼저 계산
            if (optionDTOList != null && !optionDTOList.isEmpty()) {
                for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                    RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRooMenuCartItemOptionNum())
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionDTO.getRooMenuCartItemOptionNum()));
                    optionTotalPrice += menuOption.getRoomMenuOptionPrice() * optionDTO.getRoomMenuCartItemOptionAmount();
                }
            }

            // 아이템의 최종 가격 계산
            int totalPricePerOne = basePrice + optionTotalPrice; // 메뉴 기본 가격 + 선택된 옵션 총 가격 (개당)
            int finalItemPrice = totalPricePerOne * insertCartItem.getRoomMenuCartItemAmount(); // 최종 수량 반영
            insertCartItem.setRoomMenuCartItemPrice(finalItemPrice);

            // 선택된 옵션 이름과 가격 설정 (DTO에서 복사 - 이 필드는 선택된 옵션 요약 정보를 저장하는 용도로 보임)
            insertCartItem.setRoomMenuSelectOptionName(roomMenuCartItemDTO.getRoomMenuSelectOptionName());
            insertCartItem.setRoomMenuSelectOptionPrice(roomMenuCartItemDTO.getRoomMenuSelectOptionPrice()); // DTO의 총 옵션 가격을 그대로 저장

            RoomMenuCartItem roomMenuCartItem = new RoomMenuCartItem();

            // 아이템을 저장하자.
            roomMenuCartItem = roomMenuCartItemRepository.save(insertCartItem);
            log.info("새로운 RoomMenuCartItem 저장됨. 아이템 PK: {}", roomMenuCartItem.getRoomMenuCartItemNum());

            // 아이템 옵션 저장 (RoomMenuCartItem이 저장된 후에 연결)
            if (optionDTOList != null && !optionDTOList.isEmpty()) {
                for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                    // RoomMenuOption을 DB에서 다시 조회! (안정성 확보)
                    RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRooMenuCartItemOptionNum())
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionDTO.getRooMenuCartItemOptionNum()));

                    RoomMenuCartItemOption option = new RoomMenuCartItemOption();
                    option.setRoomMenuCartItem(roomMenuCartItem); // ★ 저장된 RoomMenuCartItem에 연결 ★
                    option.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName()); // 이름 복사
                    option.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice()); // 가격 복사
                    option.setRoomMenuCartItemOptionAmount(optionDTO.getRoomMenuCartItemOptionAmount()); // 수량은 그대로

                    roomMenuCartItemOptionRepository.save(option); // 옵션 저장
                    log.info("옵션 저장됨: {} (수량: {}, 가격: {})", option.getRoomMenuCartItemOptionName(), option.getRoomMenuCartItemOptionAmount(), option.getRoomMenuCartItemOptionPrice());
                }
            }

            // 리턴값 반환
            return roomMenuCartItem.getRoomMenuCartItemNum();

        } else {
            // 6-2. 이미 장바구니에 동일한 아이템이 있다면 수량 업데이트
            log.info("장바구니에 동일한 아이템({})이 이미 있습니다. 수량을 업데이트합니다.", roomMenu.getRoomMenuName());
            RoomMenuCartItem existingItem = existingItemOptional.get(); // Optional에서 엔티티 가져오기

            // 기존 수량에 새로운 수량 추가
            int newAmount = existingItem.getRoomMenuCartItemAmount() + roomMenuCartItemDTO.getRoomMenuCartItemAmount();
            existingItem.setRoomMenuCartItemAmount(newAmount);
            existingItem.setRoomMenuCartItemCount(newAmount); // Count도 동일하게 업데이트

            // 옵션 정보 및 가격 업데이트 (동일 아이템이 있을 때 옵션 처리는 비즈니스 규칙에 따라 다름)
            // 여기서는 단순히 DTO의 옵션 정보로 기존 아이템의 요약 필드를 덮어쓰는 것으로 예시를 듭니다.
            // 기존 옵션 항목(RoomMenuCartItemOption)을 어떻게 처리할지는 별도 로직 필요 (예: 모두 삭제 후 새로 추가, 수량 누적 등)
            // 이 예시에서는 기존 RoomMenuCartItemOption 항목은 그대로 두고, RoomMenuCartItem의 요약 필드만 업데이트합니다.
            // 실제 요구사항에 맞게 이 부분을 수정해야 합니다.
            existingItem.setRoomMenuSelectOptionName(roomMenuCartItemDTO.getRoomMenuSelectOptionName());
            existingItem.setRoomMenuSelectOptionPrice(roomMenuCartItemDTO.getRoomMenuSelectOptionPrice());

            // 가격 다시 계산 (업데이트된 수량 반영)
            int basePrice = roomMenu.getRoomMenuPrice(); // 메뉴 기본 가격

            // 기존 옵션 항목들의 총 가격을 다시 계산하거나, 업데이트된 요약 옵션 가격 사용 (어떤 방식이 맞는지 설계 확인 필요)
            // 여기서는 DTO에서 받은 총 옵션 가격을 사용한다고 가정합니다.
            Integer optionTotalDTOPrice = roomMenuCartItemDTO.getRoomMenuSelectOptionPrice();

            int totalPricePerOne = basePrice + (optionTotalDTOPrice != null ? optionTotalDTOPrice : 0); // 개당 가격
            int finalPrice = totalPricePerOne * newAmount; // 업데이트된 수량 반영

            existingItem.setRoomMenuCartItemPrice(finalPrice);

            // 기존 아이템 업데이트 저장 (JPA는 변경 감지로 자동 저장되기도 하지만 명시적 save도 가능)
            roomMenuCartItemRepository.save(existingItem);
            log.info("RoomMenuCartItem 업데이트됨. 아이템 PK: {}, 새 수량: {}", existingItem.getRoomMenuCartItemNum(), newAmount);

            // 리턴값 반환 (업데이트된 아이템의 PK 반환)
            return existingItem.getRoomMenuCartItemNum();
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