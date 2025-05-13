package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.util.exception.SoldOutException;
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

/**************************************************
 * 클래스명 : RoomMenuCartServiceImpl
 * 기능   : 룸서비스 장바구니 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다.
 * (RoomMenuCartService 인터페이스 구현)
 * 장바구니 아이템 추가, 장바구니 아이템 목록 상세 조회(옵션 포함, 페이지네이션), 장바구니 아이템 소유자 검증,
 * 장바구니 아이템 수량 변경 및 삭제, 메뉴 상세 정보 조회(옵션 및 다국어 지원),
 * 장바구니 내 총 아이템 개수 조회, 장바구니 총액 계산 및 쿠폰 적용 시 최종 금액 계산 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 : 2025-05-09
 * 주요 메소드/기능 : RoomMenuCartRead, RoomMenuCartInsert, RoomMenuCartItemList,
 * verificationRoomMenuCartItem, RoomMenuCartItemAmountUpdate, RoomCartMenuCartItemDelete,
 * read, getTotalCartItemCount, getTotalPriceWithCoupon, getCartTotal
 **************************************************/

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
    private final RoomMenuCartItemOptionRepository roomMenuCartItemOptionRepository;


    /**************************************************
     * 메소드명 : RoomMenuCartRead
     * 사용자 이메일 기반 룸서비스 메뉴 정보 조회 (단일 메뉴)
     * 기능: 제공된 회원 이메일(`email`)을 사용하여 해당 회원의 객실과 연관된 룸서비스 메뉴 정보
     * (현재 구현 상 첫 번째 또는 특정 대표 메뉴로 추정)를 조회하고 `RoomMenuDTO`로 변환하여 반환합니다.
     * @param email String : 조회할 회원의 이메일.
     * @return RoomMenuDTO : 조회된 룸서비스 메뉴 DTO. 해당 메뉴가 없으면 null을 반환할 수 있습니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : -
     **************************************************/

    @Override
    public RoomMenuDTO RoomMenuCartRead(String email) {
        RoomMenu roomMenu =
                roomMenuRepository.findByRoom_Member_MemberEmail(email);

        log.info(email);

        RoomMenuDTO roomMenuDTO =
                modelMapper.map(roomMenu, RoomMenuDTO.class);

        return roomMenuDTO;
    }

    /**************************************************
     * 메소드명 : RoomMenuCartInsert
     * 장바구니 아이템 추가 (옵션 포함)
     * 기능: 로그인한 회원(`email`)의 장바구니에 새로운 아이템(`roomMenuCartItemDTO`)을 추가합니다.
     * 회원의 장바구니가 없으면 새로 생성합니다. 선택된 옵션 정보를 포함하여
     * `RoomMenuCartItem` 및 `RoomMenuCartItemOption` 엔티티를 생성하고 저장합니다.
     * 최종 아이템 가격은 (기본가 + 총 옵션가) * 수량으로 계산됩니다.
     * @param email String : 아이템을 추가할 회원의 이메일.
     * @param roomMenuCartItemDTO RoomMenuCartItemDTO : 추가할 장바구니 아이템의 정보 (메뉴 ID, 수량, 선택된 옵션 목록 등).
     * @return Long : 새로 생성된 `RoomMenuCartItem`의 ID.
     * @throws IllegalArgumentException : 회원 정보가 없거나, DTO에 상품 ID가 없거나, 옵션이 상품에 속하지 않는 경우.
     * @throws EntityNotFoundException : DTO에 명시된 메뉴나 옵션을 찾을 수 없는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-28
     **************************************************/

    @Override
    public Long RoomMenuCartInsert(String email, RoomMenuCartItemDTO roomMenuCartItemDTO) {
        log.info("장바구니 추가 시스템 도입 (항상 새 아이템으로 추가) - 회원 이메일: {}", email);

        // 1. 로그인한 회원 조회
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) throw new IllegalArgumentException("회원 정보가 존재하지 않습니다.");
        log.info("멤버 찾음: {}", member.getMemberName());

        // 2. 해당 회원의 장바구니 가져오기 (없으면 생성)
        RoomMenuCart roomMenuCart = roomMenuCartRepository.findByMember_MemberEmail(email);
        if (roomMenuCart == null) {
            log.info("회원의 장바구니가 존재하지 않습니다. 새로 생성합니다.");
            RoomMenuCart insertCart = new RoomMenuCart();
            insertCart.setMember(member);
            roomMenuCart = roomMenuCartRepository.save(insertCart);
        }
        log.info("장바구니 찾음 또는 생성됨. ID: {}", roomMenuCart.getRoomMenuCartNum());


        // ★★★ DTO에서 상품 ID를 가져옵니다. (RoomMenuCartItemDTO에 getRoomMenuNum()이 상품 ID를 반환하도록 구현되어 있어야 함) ★★★
        Long productId = roomMenuCartItemDTO.getRoomMenuNum();
        if (productId == null) {
            log.error("DTO에서 상품 ID(roomMenuNum)를 가져오지 못했습니다. DTO 필드 매핑을 확인하세요. DTO: {}", roomMenuCartItemDTO.toString()); // DTO 내용 로깅
            throw new IllegalArgumentException("상품 ID(roomMenuNum)가 요청에 포함되지 않았거나 DTO에 매핑되지 않았습니다.");
        }
        log.info("DTO로부터 상품 ID {} 가져옴", productId);

        // 3. 메뉴 정보 조회 (★★★ productId 변수 사용 ★★★)
        RoomMenu roomMenu = roomMenuRepository.findById(productId) // <<< 수정된 부분: productId 사용
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 조회 시도 ID: {}", productId);
                    return new EntityNotFoundException("메뉴를 찾을 수 없습니다. ID: " + productId);
                });
        log.info("메뉴 찾음: {}, ID: {}", roomMenu.getRoomMenuName(), roomMenu.getRoomMenuNum());


        // --- 아래 중복된 메뉴 조회 로직은 제거하거나 roomMenu 변수를 재활용합니다. ---
        // Long menuIdFromDto = roomMenuCartItemDTO.getRoomMenuCartItemNum();
        // if (menuIdFromDto == null) throw new IllegalArgumentException("상품 ID가 DTO에 없습니다.");
        // RoomMenu roomMenuA = roomMenuRepository.findById(menuIdFromDto)
        //         .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. ID: " + menuIdFromDto));
        // log.info("메뉴 찾음: {}", roomMenuA.getRoomMenuName());


        List<RoomMenuCartItemOptionDTO> optionDTOList = roomMenuCartItemDTO.getSelectedOptions();

        StringBuilder selectedOptionName = new StringBuilder();
        int totalSelectedOptionPrice = 0;

        if (optionDTOList != null && !optionDTOList.isEmpty()) {
            for (int i = 0; i < optionDTOList.size(); i++) {
                RoomMenuCartItemOptionDTO optionDTO = optionDTOList.get(i);
                selectedOptionName.append(optionDTO.getRoomMenuCartItemOptionName());
                if (optionDTO.getRoomMenuCartItemOptionPrice() != null && optionDTO.getRoomMenuCartItemOptionPrice() != 0) {
                    selectedOptionName.append(" (+").append(optionDTO.getRoomMenuCartItemOptionPrice()).append(")");
                }
                totalSelectedOptionPrice += (optionDTO.getRoomMenuCartItemOptionPrice() != null ? optionDTO.getRoomMenuCartItemOptionPrice() : 0) * optionDTO.getRoomMenuCartItemOptionAmount();
                if (i < optionDTOList.size() - 1) {
                    selectedOptionName.append(", ");
                }
            }
        } else {
            log.info("선택된 옵션이 없습니다.");
        }

        log.info("새 아이템을 장바구니에 추가합니다. 상품: {}, 수량: {}", roomMenu.getRoomMenuName(), roomMenuCartItemDTO.getRoomMenuCartItemAmount());
        RoomMenuCartItem insertCartItem = new RoomMenuCartItem();
        insertCartItem.setRoomMenuCart(roomMenuCart);
        insertCartItem.setRoomMenu(roomMenu); // 올바르게 조회된 roomMenu 사용
        insertCartItem.setRoomMenuCartItemAmount(roomMenuCartItemDTO.getRoomMenuCartItemAmount());

        insertCartItem.setRoomMenuSelectOptionName(selectedOptionName.toString());
        insertCartItem.setRoomMenuSelectOptionPrice(totalSelectedOptionPrice);

        int finalItemTotalPrice = (roomMenu.getRoomMenuPrice() + totalSelectedOptionPrice) * roomMenuCartItemDTO.getRoomMenuCartItemAmount();
        insertCartItem.setRoomMenuCartItemPrice(finalItemTotalPrice);

        RoomMenuCartItem savedCartItem = roomMenuCartItemRepository.save(insertCartItem);
        log.info("새 장바구니 아이템 저장 완료. ID: {}", savedCartItem.getRoomMenuCartItemNum());

        if (optionDTOList != null && !optionDTOList.isEmpty()) {
            // ... (옵션 저장 로직은 이전과 동일하게 유지, 단, optionDTO.getRoomMenuOptionNum()이 null이 아님을 위에서 확인했거나 여기서도 확인) ...
            log.info("새 아이템에 대한 RoomMenuCartItemOption {}개를 저장합니다.", optionDTOList.size());
            for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRoomMenuCartItemOptionNum()) // 이 ID도 DTO에서 잘 오는지 확인
                        .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionDTO.getRoomMenuCartItemOptionNum()));

                if (!menuOption.getRoomMenu().getRoomMenuNum().equals(roomMenu.getRoomMenuNum())) {
                    throw new IllegalArgumentException("옵션 '" + menuOption.getRoomMenuOptionName() + "'는 상품 '" + roomMenu.getRoomMenuName() + "'의 옵션이 아닙니다.");
                }

                RoomMenuCartItemOption option = new RoomMenuCartItemOption();
                option.setRoomMenuCartItem(savedCartItem);
                option.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName());
                option.setRoomMenuOption(menuOption);
                option.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice());
                option.setRoomMenuCartItemOptionAmount(optionDTO.getRoomMenuCartItemOptionAmount() != null ? optionDTO.getRoomMenuCartItemOptionAmount() : 1); // 옵션 수량 null 체크
                roomMenuCartItemOptionRepository.save(option);
            }
            log.info("RoomMenuCartItemOption 저장 완료.");
        }

        return savedCartItem.getRoomMenuCartItemNum();

    }

    /**************************************************
     * 메소드명 : RoomMenuCartItemList
     * 회원 장바구니 아이템 목록 상세 조회 (옵션 포함, 페이지네이션)
     * 기능: 특정 회원(`email`)의 장바구니에 담긴 아이템 목록을 페이지네이션하여 조회합니다.
     * 각 장바구니 아이템(`RoomMenuCartDetailDTO`)에 대해 연관된 선택 옵션(`RoomMenuCartItemOptionDTO`) 목록을
     * 함께 조회하여 DTO에 포함시켜 반환합니다.
     * @param email String : 장바구니 목록을 조회할 회원의 이메일.
     * @param pageable Pageable : 페이징 처리 정보 (페이지 번호, 페이지 크기 등).
     * @return Page<RoomMenuCartDetailDTO> : 조회된 장바구니 아이템 상세 DTO 페이지 객체.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-27
     **************************************************/

    @Override
    public Page<RoomMenuCartDetailDTO> RoomMenuCartItemList(String email, Pageable pageable) {
        log.info("특정 회원 아이템 목록 조회 서비스 진입" + email);

        Page<RoomMenuCartDetailDTO> roomMenuCartDetailDTOPage =
                roomMenuCartItemRepository.findByCartDetailDTOList(email, pageable);

        roomMenuCartDetailDTOPage.forEach(dto -> {
            List<RoomMenuCartItemOption> optionList =
                    roomMenuCartItemOptionRepository.findDistinctByRoomMenuCartItem_RoomMenuCartItemNum(dto.getRoomMenuCartDetailNum());

            List<RoomMenuCartItemOptionDTO> optionDTOs = optionList.stream()
                    .map(opt -> new RoomMenuCartItemOptionDTO(
                            opt.getRoomMenuCartItemOptionName(),
                            opt.getRoomMenuCartItemOptionPrice(),
                            opt.getRoomMenuCartItemOptionAmount()
                    )).collect(Collectors.toList());

            dto.setOptionList(optionDTOs); // 옵션 리스트 주입

        });

        return roomMenuCartDetailDTOPage;
    }


    /**************************************************
     * 메소드명 : verificationRoomMenuCartItem
     * 장바구니 아이템 소유자 검증
     * 기능: 특정 장바구니 아이템 ID(`RoomMenuCartItemNum`)가 주어진 이메일(`email`)을 가진
     * 회원의 장바구니에 속하는지를 검증합니다.
     * @param RoomMenuCartItemNum Long : 검증할 장바구니 아이템의 ID.
     * @param email String : 검증할 회원의 이메일.
     * @return boolean : 해당 회원의 장바구니 아이템이면 true, 아니면 false.
     * @throws EntityNotFoundException : `RoomMenuCartItemNum`에 해당하는 아이템이 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : -
     **************************************************/

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

    /**************************************************
     * 메소드명 : RoomMenuCartItemAmountUpdate
     * 장바구니 아이템 수량 변경
     * 기능: 특정 장바구니 아이템(`RoomMenuCartItemNum`)의 수량(`roomMenuCartItemAmount`)을 업데이트합니다.
     * 수량 변경에 따른 총 가격 재계산 및 반영 로직도 함께 처리합니다.
     * @throws EntityNotFoundException : `RoomMenuCartItemNum`에 해당하는 아이템이 존재하지 않는 경우.
     * @throws IllegalArgumentException : 수량이 유효하지 않은 경우 (예: 음수).
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : -
     **************************************************/

    @Override
    public void RoomMenuCartItemAmountUpdate(Long roomMenuCartItemNum, Integer newAmount) {
        {
            log.info("장바구니 아이템 ID {}의 수량을 {}로 변경하는 서비스 진입", roomMenuCartItemNum, newAmount);

            if (newAmount == null || newAmount <= 0) {
                // 수량이 0 이하이면 아이템 삭제 처리 또는 예외 발생
                // 여기서는 예외를 발생시키거나, 최소 수량을 1로 강제하는 등의 정책이 필요합니다.
                // 일단 최소 수량 1로 가정하지 않고, 0이하 요청 시 오류로 처리하겠습니다.
                // 또는, 수량이 0이 되면 해당 아이템을 장바구니에서 삭제하는 RoomCartMenuCartItemDelete(roomMenuCartItemNum)를 호출할 수도 있습니다.
                log.warn("잘못된 수량 요청: ID {}, 수량 {}", roomMenuCartItemNum, newAmount);
                throw new IllegalArgumentException("수량은 1 이상이어야 합니다. 아이템을 삭제하려면 삭제 버튼을 이용해주세요.");
            }

            RoomMenuCartItem cartItem = roomMenuCartItemRepository.findById(roomMenuCartItemNum)
                    .orElseThrow(() -> {
                        log.error("장바구니 아이템 ID {}를 찾을 수 없습니다.", roomMenuCartItemNum);
                        return new EntityNotFoundException("장바구니 아이템을 찾을 수 없습니다. ID: " + roomMenuCartItemNum);
                    });

            RoomMenu product = cartItem.getRoomMenu();
            if (product == null) {
                log.error("장바구니 아이템 ID {}에 연결된 상품 정보가 없습니다.", roomMenuCartItemNum);
                throw new EntityNotFoundException("장바구니 아이템에 연결된 상품 정보를 찾을 수 없습니다.");
            }

            // 상품 재고 확인
            if (product.getRoomMenuAmount() < newAmount) {
                log.warn("상품 '{}'(ID:{}) 재고 부족. 요청 수량: {}, 현재 재고: {}",
                        product.getRoomMenuName(), product.getRoomMenuNum(), newAmount, product.getRoomMenuAmount());
                throw new SoldOutException("상품 '" + product.getRoomMenuName() + "'의 재고가 부족합니다. (요청 수량: " + newAmount + ", 현재 재고: " + product.getRoomMenuAmount() + ")");
            }
            // 여기에 연결된 옵션들의 재고도 함께 고려해야 한다면 추가 로직 필요 (현재는 주 상품 재고만 체크)

            cartItem.setRoomMenuCartItemAmount(newAmount); // 새 수량 설정

            // 아이템 가격 재계산: (상품 기본 단가 * 새 수량) + 아이템의 총 옵션 추가 금액
            // cartItem.getRoomMenuSelectOptionPrice() 필드가 "해당 아이템의 모든 옵션에 대한 총 추가 금액"을 이미 담고 있다고 가정합니다.
            // 이 값은 옵션 변경 시 RoomMenuCartServiceImpl.updateCartItemWithOptions 에서 계산되어 저장되었습니다.
            int itemBasePrice = product.getRoomMenuPrice();
            int totalOptionPriceForItem = cartItem.getRoomMenuSelectOptionPrice() != null ? cartItem.getRoomMenuSelectOptionPrice() : 0;

            int newItemTotalPrice = (itemBasePrice * newAmount) + totalOptionPriceForItem;
            cartItem.setRoomMenuCartItemPrice(newItemTotalPrice); // 재계산된 최종 아이템 가격 설정
            log.info("장바구니 아이템 ID {} 가격 재계산 완료. 상품 기본가: {}, 새 수량: {}, 총 옵션가: {}, 새 총 아이템 가격: {}",
                    roomMenuCartItemNum, itemBasePrice, newAmount, totalOptionPriceForItem, newItemTotalPrice);

            roomMenuCartItemRepository.save(cartItem); // 변경된 RoomMenuCartItem 저장 (수량 및 가격)

            // 전체 장바구니(RoomMenuCart)의 총액 업데이트
            RoomMenuCart cart = cartItem.getRoomMenuCart();
            if (cart != null) {
                List<RoomMenuCartItem> updatedCartItems = roomMenuCartItemRepository.findByRoomMenuCart(cart);
                int finalCartTotalPrice = updatedCartItems.stream()
                        .mapToInt(ci -> ci.getRoomMenuCartItemPrice() != null ? ci.getRoomMenuCartItemPrice() : 0)
                        .sum();
                cart.setRoomMenuCartTotalPrice(finalCartTotalPrice);
                roomMenuCartRepository.save(cart);
                log.info("장바구니 ID {}의 전체 총액 업데이트 완료: {}", cart.getRoomMenuCartNum(), cart.getRoomMenuCartTotalPrice());
            } else {
                log.warn("장바구니 아이템 ID {}에 연결된 장바구니(RoomMenuCart)가 없습니다. 전체 총액 업데이트를 건너<0xE1><0xB9><0xA5>니다.", roomMenuCartItemNum);
            }
        }

    }

    /**************************************************
     * 메소드명 : RoomCartMenuCartItemDelete
     * 장바구니 아이템 삭제
     * 기능: 특정 장바구니 아이템(`roomMenuCartItemNum`)을 데이터베이스에서 삭제합니다.
     * 해당 아이템에 연결된 `RoomMenuCartItemOption`들도 함께 삭제됩니다(cascade 설정에 따름).
     * @param roomMenuCartItemNum Long : 삭제할 장바구니 아이템의 ID.
     * @throws EntityNotFoundException : `roomMenuCartItemNum`에 해당하는 아이템이 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : -
     **************************************************/

    @Override
    public void RoomCartMenuCartItemDelete(Long roomMenuCartItemNum) {
        log.info("서비스로 들어온 pk: {}", roomMenuCartItemNum);

        // 주어진 roomMenuCartItemNum을 기반으로 장바구니 아이템을 조회
        RoomMenuCartItem roomMenuCartItem =
                roomMenuCartItemRepository.findById(roomMenuCartItemNum).orElseThrow(EntityNotFoundException::new);

        // 장바구니 아이템을 삭제
        roomMenuCartItemRepository.delete(roomMenuCartItem);
        log.info("아이템 삭제 완료: {}", roomMenuCartItemNum);

    }

    /**************************************************
     * 메소드명 : read
     * 룸서비스 메뉴 상세 정보 조회 (옵션 및 다국어 지원)
     * 기능: 특정 룸서비스 메뉴 번호(`num`)를 기준으로 메뉴의 상세 정보를 조회합니다.
     * 현재 로케일(`locale`)에 맞는 번역된 메뉴 이름과 내용을 적용하고,
     * 해당 메뉴에 속한 모든 옵션 목록을 함께 조회하여 `RoomMenuDTO`로 구성합니다.
     * 조회된 정보는 파라미터로 전달된 `Model` 객체에도 추가됩니다. (서비스에서 Model 객체 직접 사용은 비권장)
     * @param num Long : 조회할 룸서비스 메뉴의 ID.
     * @param locale Locale : 다국어 처리를 위한 현재 로케일 정보.
     * @param model Model : (비권장) 뷰에 데이터를 전달하기 위한 모델 객체. 서비스 결과는 DTO로만 반환하는 것이 좋음.
     * @return RoomMenuDTO : 조회된 룸서비스 메뉴의 상세 정보 DTO (번역 및 옵션 포함).
     * @throws EntityNotFoundException : `num`에 해당하는 메뉴가 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-09
     * 수정일 : 2025-04-27
     **************************************************/

    // 상세보기
    @Override
    public RoomMenuDTO read(Long num, Locale locale, Model model) {
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
                .distinct() // 먼저 엔티티 중복 제거
                .map(option -> modelMapper.map(option, RoomMenuOptionDTO.class)) // 그 다음 DTO로 변환
                .collect(Collectors.toList());

        roomMenuDTO.setOptions(optionDTOs);  // RoomMenuDTO에 옵션 리스트 추가

        model.addAttribute("roomMenuDTO", roomMenuDTO);

        return roomMenuDTO;  // 최종적으로 옵션을 포함한 DTO 반환
    }

    /**************************************************
     * 메소드명 : getTotalCartItemCount
     * 회원 장바구니 총 아이템 개수 조회
     * 기능: 특정 회원(`memberEmail`)의 장바구니에 담긴 모든 아이템들의 총 개수(각 아이템의 수량을 합산)를
     * 조회하여 반환합니다. 장바구니가 비어있거나 아이템이 없으면 0을 반환합니다.
     * @param memberEmail String : 조회할 회원의 이메일.
     * @return Integer : 장바구니 내 총 아이템 개수 (각 아이템의 수량 합).
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-15
     * 수정일 : -
     **************************************************/

    @Override
    public Integer getTotalCartItemCount(String memberEmail) {
        log.info("장바구니 아이템 조회 서비스 진입");
        Integer count = roomMenuCartItemRepository.getTotalItemCountByMemberEmail(memberEmail);
        return count != null ? count : 0;
    }

    /**************************************************
     * 메소드명 : getTotalPriceWithCoupon
     * (장바구니 서비스 내) 쿠폰 적용 시 최종 결제 금액 계산
     * 기능: 특정 회원(`email`)의 장바구니 총액에 지정된 쿠폰(`couponNum`)을 적용했을 때의
     * 할인된 최종 결제 금액을 계산하여 반환합니다.
     * 이 메소드는 쿠폰의 유효성(소유권, 만료일, 사용여부)을 직접 검사하지 않고,
     * `getCartTotal`을 호출하여 장바구니 총액을 계산하고, CouponRepository에서 쿠폰 정보를 가져와
     * 할인율을 적용합니다.
     * (주의: 쿠폰 유효성 검증은 CouponService에서 수행하거나 이 메소드 내에 추가해야 합니다.)
     * @param email String : 할인을 적용할 회원의 이메일.
     * @param couponNum Long : 적용할 쿠폰의 번호(ID).
     * @return Integer : 쿠폰 할인이 적용된 최종 결제 금액.
     * @throws RuntimeException : 회원이 존재하지 않거나, 쿠폰을 찾을 수 없는 경우, 또는 장바구니가
     * 없는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : 2025-04-29
     **************************************************/

    // 쿠폰 할인
    @Override
    public Integer getTotalPriceWithCoupon(String email, Long couponNum) {
        log.info("쿠폰 적용 최종금액 서비스 진입" + couponNum);

        Member member = memberRepository.findByMemberEmail(email);
        Coupon coupon = couponRepository.findById(couponNum).orElseThrow();

        int originalPrice = getCartTotal(member); // 장바구니 금액 계산 메서드
        int discount = (originalPrice * coupon.getDiscountRate()) / 100;
        int finalPrice = originalPrice - discount; // 최종 가격 계산

        log.info("쿠폰 적용 결과: 원 가격 {}, 할인 {}, 최종 {}", originalPrice, discount, finalPrice);
        return finalPrice;

    }

    /**************************************************
     * 메소드명 : getCartTotal (Member 파라미터)
     * (장바구니 서비스 내) 장바구니 총액 계산
     * 기능: 특정 `Member` 객체를 기준으로 해당 회원의 장바구니에 담긴 모든 상품들의
     * 총액(각 상품의 메뉴 기본 가격 * 수량)을 계산하여 반환합니다.
     * (주의: 현재 로직은 각 아이템의 옵션 가격을 총액 계산에 포함하지 않고 있습니다.)
     * @param member Member : 장바구니 총액을 계산할 회원 엔티티.
     * @return Integer : 계산된 장바구니 총액.
     * @throws RuntimeException : 해당 회원의 장바구니가 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

    @Override
    public Integer getCartTotal(Member member) {
        log.info("장바구니 최종 결제 금액 계산 서비스 진입" + member);
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

    @Override
    public List<RoomMenuOptionDTO> getAvailableOptionsForProduct(Long roomMenuNum) {
        log.info("상품 ID {}에 대한 사용 가능 옵션 목록 조회 서비스 진입", roomMenuNum);
        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + roomMenuNum));

        List<RoomMenuOption> options = roomMenuOptionRepository.findByRoomMenu(roomMenu);
        return options.stream()
                .map(option -> {
                    RoomMenuOptionDTO dto = modelMapper.map(option, RoomMenuOptionDTO.class);
                    // RoomMenuOptionDTO에 옵션 ID(roomMenuOptionNum), 재고(roomMenuOptionAmount) 등이 포함되도록 modelMapper 설정 또는 직접 매핑
                    dto.setRoomMenuOptionNum(option.getRoomMenuOptionNum()); // ★ RoomMenuOptionDTO에 이 필드가 있어야 함
                    dto.setRoomMenuOptionAmount(option.getRoomMenuOptionAmount()); // ★ RoomMenuOptionDTO에 이 필드가 있어야 함
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateCartItemWithOptions(Long cartItemId, String userEmail, List<UpdateCartOptionRequestDTO> selectedOptionUpdates) { // ★ DTO 타입 변경
        log.info("장바구니 아이템 ID {} 옵션 업데이트 시작. 사용자: {}.", cartItemId, userEmail);

        if (selectedOptionUpdates != null) {
            log.info("수신된 selectedOptionUpdates 리스트 크기: {}", selectedOptionUpdates.size());
            for (int i = 0; i < selectedOptionUpdates.size(); i++) {
                UpdateCartOptionRequestDTO dto = selectedOptionUpdates.get(i); // ★ 타입 변경
                if (dto != null) {
                    log.info("수신된 DTO 인덱스 [{}]: roomMenuOptionNum={}, roomMenuCartItemOptionAmount={}",
                            i, dto.getRoomMenuOptionNum(), dto.getRoomMenuCartItemOptionAmount());
                } else {
                    log.warn("수신된 DTO 인덱스 [{}]가 null입니다.", i);
                }
            }
        } else {
            log.warn("수신된 selectedOptionUpdates 리스트가 null입니다.");
        }
        // ★★★ 로깅 끝 ★★★

        // 1. 장바구니 아이템 소유권 검증 및 조회
        if (!verificationRoomMenuCartItem(cartItemId, userEmail)) {
            throw new IllegalArgumentException("해당 장바구니 아이템에 대한 접근 권한이 없습니다.");
        }
        RoomMenuCartItem cartItem = roomMenuCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 아이템을 찾을 수 없습니다. ID: " + cartItemId));
        log.info("대상 장바구니 아이템: {}, 현재 상품: {}", cartItem.getRoomMenuCartItemNum(), cartItem.getRoomMenu().getRoomMenuName());

        // 2. 기존 선택된 옵션들 삭제 (RoomMenuCartItemOptionRepository 사용)
        List<RoomMenuCartItemOption> existingOptions = roomMenuCartItemOptionRepository.findByRoomMenuCartItem(cartItem);
        if (!existingOptions.isEmpty()) {
            log.info("기존 선택된 옵션 {}개 삭제 시도", existingOptions.size());
            roomMenuCartItemOptionRepository.deleteAll(existingOptions); // 또는 deleteAllInBatch(existingOptions)
        }

        // 3. 새로운 옵션 정보로 RoomMenuCartItemOption 생성 및 저장
        StringBuilder newSelectedOptionName = new StringBuilder();
        int newTotalSelectedOptionPrice = 0; // 옵션들의 (개당 가격 * 수량)의 총합

        if (selectedOptionUpdates != null && !selectedOptionUpdates.isEmpty()) {
            log.info("For 루프 진입 전 selectedOptionUpdates: {}", selectedOptionUpdates);
            for (UpdateCartOptionRequestDTO optionUpdateDTO : selectedOptionUpdates) { // ★ 타입 변경
                log.info("For 루프 시작 - 현재 optionUpdateDTO: {}", optionUpdateDTO);
                if (optionUpdateDTO == null || optionUpdateDTO.getRoomMenuCartItemOptionAmount() == null || optionUpdateDTO.getRoomMenuCartItemOptionAmount() <= 0) {
                    log.warn("잘못된 DTO 또는 수량 0 이하, 건너<0xE1><0xB9><0xA5>니다: {}", optionUpdateDTO);
                    continue;
                }

                Long optionIdFromDto = optionUpdateDTO.getRoomMenuOptionNum();
                Integer optionAmountFromDto = optionUpdateDTO.getRoomMenuCartItemOptionAmount();
                log.info("DTO에서 추출한 옵션 ID: {}, 수량: {}", optionIdFromDto, optionAmountFromDto);

                if (optionIdFromDto == null) {
                    log.error("CRITICAL: DTO에서 추출한 옵션 ID가 null입니다! DTO: {}", optionUpdateDTO);
                    throw new IllegalArgumentException("옵션 ID가 null입니다.");
                }

                RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionIdFromDto)
                        .orElseThrow(() -> {
                            log.error("DB에서 옵션 ID {}를 찾을 수 없습니다.", optionIdFromDto);
                            return new EntityNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionIdFromDto);
                        });

                log.info("DB에서 조회된 menuOption (ID: {}): 이름='{}', 가격={}, 현재재고={}",
                        menuOption.getRoomMenuOptionNum(), menuOption.getRoomMenuOptionName(),
                        menuOption.getRoomMenuOptionPrice(), menuOption.getRoomMenuOptionAmount());


                // 옵션 재고 확인
                if (menuOption.getRoomMenuOptionAmount() < optionUpdateDTO.getRoomMenuCartItemOptionAmount()) {
                    throw new SoldOutException("옵션 '" + menuOption.getRoomMenuOptionName() + "'의 재고가 부족합니다. (요청: " + optionUpdateDTO.getRoomMenuCartItemOptionAmount() + ", 현재: " + menuOption.getRoomMenuOptionAmount() + ")");
                }

                log.info("가격 누적 전: newTotalSelectedOptionPrice={}, menuOption.getRoomMenuOptionPrice()={}, optionUpdateDTO.getRoomMenuCartItemOptionAmount()={}",
                        newTotalSelectedOptionPrice,
                        menuOption.getRoomMenuOptionPrice(),
                        optionUpdateDTO.getRoomMenuCartItemOptionAmount());

                newTotalSelectedOptionPrice += (menuOption.getRoomMenuOptionPrice() * optionUpdateDTO.getRoomMenuCartItemOptionAmount());
                log.info("가격 누적 후 newTotalSelectedOptionPrice: {}", newTotalSelectedOptionPrice); // ★ 누적된 가격 확인

                log.info("옵션 이름 추가 전 newSelectedOptionName: '{}'", newSelectedOptionName.toString());
                log.info("추가할 옵션 이름: '{}'", menuOption.getRoomMenuOptionName());

                log.info("cartItem에 설정 전: newSelectedOptionName='{}', newTotalSelectedOptionPrice={}", newSelectedOptionName.toString().trim().replaceAll(", $", ""), newTotalSelectedOptionPrice);
                cartItem.setRoomMenuSelectOptionName(newSelectedOptionName.toString().trim().replaceAll(", $", ""));
                cartItem.setRoomMenuSelectOptionPrice(newTotalSelectedOptionPrice); // ★ 이 필드의 의미가 "총 옵션 추가금액"인지 확인

                log.info("cartItem에 설정 후: getRoomMenuSelectOptionName='{}', getRoomMenuSelectOptionPrice={}", cartItem.getRoomMenuSelectOptionName(), cartItem.getRoomMenuSelectOptionPrice());

                int baseProductPrice = cartItem.getRoomMenu().getRoomMenuPrice();
                int productAmount = cartItem.getRoomMenuCartItemAmount();
                log.info("최종 아이템 가격 계산 전: 상품단가={}, 상품수량={}, 총옵션추가금={}", baseProductPrice, productAmount, newTotalSelectedOptionPrice);


                RoomMenuCartItemOption newCartOption = new RoomMenuCartItemOption();
                newCartOption.setRoomMenuCartItem(cartItem);
                newCartOption.setRoomMenuOption(menuOption); // 실제 옵션 마스터 연결
                newCartOption.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName());
                newCartOption.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice()); // 개당 가격
                newCartOption.setRoomMenuCartItemOptionAmount(optionUpdateDTO.getRoomMenuCartItemOptionAmount()); // 선택 수량
                roomMenuCartItemOptionRepository.save(newCartOption);

                if (newSelectedOptionName.length() > 0 && optionAmountFromDto > 0) { // 이미 다른 옵션이 있고, 현재 옵션도 유효하면 콤마 추가
                    // (주의) 이 조건은 selectedOptionUpdates 리스트의 현재 인덱스를 보고 다음 유효 옵션이 있는지 판단하는 것이 더 정확함
                    // 간단하게는, 항상 추가하고 마지막에 trim으로 ", "를 제거하는 방법도 있음
                    newSelectedOptionName.append(", ");
                }
                if(optionAmountFromDto > 0) { // 수량이 0보다 클 때만 이름 추가
                    newSelectedOptionName.append(menuOption.getRoomMenuOptionName());
                    if (menuOption.getRoomMenuOptionPrice() > 0) {
                        newSelectedOptionName.append(" (+").append(menuOption.getRoomMenuOptionPrice()).append("원 x").append(optionAmountFromDto).append(")");
                    } else {
                        newSelectedOptionName.append(" (x").append(optionAmountFromDto).append(")");
                    }
                }

                // ★★★ 아래 로그 추가 ★★★
                log.info("현재까지 누적된 newTotalSelectedOptionPrice: {}", newTotalSelectedOptionPrice);
                log.info("현재까지 누적된 newSelectedOptionName: {}", newSelectedOptionName.toString());


                log.info("cartItem에 설정할 최종 newSelectedOptionName: {}", newSelectedOptionName.toString().trim().replaceAll(", $", ""));
                log.info("cartItem에 설정할 최종 newTotalSelectedOptionPrice (또는 계산된 값): {}", cartItem.getRoomMenuSelectOptionPrice()); // set 하기 전 값이므로, 계산된 값을 직접 로깅


            }

            String finalOptionNameSummary = newSelectedOptionName.toString().trim();
            if (finalOptionNameSummary.startsWith(",")) { // 혹시 맨 앞에 콤마가 붙었다면 제거
                finalOptionNameSummary = finalOptionNameSummary.substring(1).trim();
            }
            if (finalOptionNameSummary.endsWith(",")) { // 혹시 맨 뒤에 콤마가 붙었다면 제거
                finalOptionNameSummary = finalOptionNameSummary.substring(0, finalOptionNameSummary.length() -1).trim();
            }

            log.info("cartItem에 설정 전: newSelectedOptionName='{}', newTotalSelectedOptionPrice={}", finalOptionNameSummary, newTotalSelectedOptionPrice);
            cartItem.setRoomMenuSelectOptionName(finalOptionNameSummary);
            cartItem.setRoomMenuSelectOptionPrice(newTotalSelectedOptionPrice);


            // 아이템의 최종 가격 업데이트: (상품 기본 단가 * 수량) + (총 옵션 추가금)
            int updatedItemTotalPrice = (cartItem.getRoomMenu().getRoomMenuPrice() * cartItem.getRoomMenuCartItemAmount()) + newTotalSelectedOptionPrice;
            cartItem.setRoomMenuCartItemPrice(updatedItemTotalPrice);
            roomMenuCartItemRepository.save(cartItem);
            log.info("장바구니 아이템 ID {}의 옵션 및 가격 업데이트 완료. 새 옵션 요약: '{}', 새 총 아이템 가격: {}", cartItemId, cartItem.getRoomMenuSelectOptionName(), cartItem.getRoomMenuCartItemPrice());

            // 5. 전체 장바구니(RoomMenuCart)의 총액 업데이트
            RoomMenuCart cart = cartItem.getRoomMenuCart();
            List<RoomMenuCartItem> updatedCartItems = roomMenuCartItemRepository.findByRoomMenuCart(cart);
            int finalCartTotalPrice = updatedCartItems.stream()
                    .mapToInt(RoomMenuCartItem::getRoomMenuCartItemPrice)
                    .sum();
            cart.setRoomMenuCartTotalPrice(finalCartTotalPrice);
            roomMenuCartRepository.save(cart);
            log.info("장바구니 ID {}의 전체 총액 업데이트 완료: {}", cart.getRoomMenuCartNum(), cart.getRoomMenuCartTotalPrice());

        }

    }
}