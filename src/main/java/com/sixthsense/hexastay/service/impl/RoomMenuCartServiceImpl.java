package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.RoomMenuCartService;
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

            // 3. 메뉴 정보 조회
            RoomMenu roomMenu =
                    roomMenuRepository.findById(roomMenuCartItemDTO.getRoomMenuCartItemNum())
                            .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. ID: " + roomMenuCartItemDTO.getRoomMenuCartItemNum()));
            log.info("메뉴 찾음: {}", roomMenu.getRoomMenuName());

        Long menuIdFromDto = roomMenuCartItemDTO.getRoomMenuCartItemNum(); //
        if (menuIdFromDto == null) throw new IllegalArgumentException("상품 ID가 DTO에 없습니다.");

        RoomMenu roomMenuA = roomMenuRepository.findById(menuIdFromDto)
                .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. ID: " + menuIdFromDto));
        log.info("메뉴 찾음: {}", roomMenuA.getRoomMenuName());

            List<RoomMenuCartItemOptionDTO> optionDTOList = roomMenuCartItemDTO.getSelectedOptions(); // DTO에서 선택된 옵션 목록 가져옴

            // --- RoomMenuCartItem의 옵션 요약 정보 설정 (새 아이템용) ---
            StringBuilder selectedOptionName = new StringBuilder();
            int totalSelectedOptionPrice = 0; // 선택된 옵션들의 총 가격 (개당 기준)

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
            // --- 옵션 요약 정보 설정 끝 ---

            log.info("새 아이템을 장바구니에 추가합니다."); // 이 로그는 항상 실행
            RoomMenuCartItem insertCartItem = new RoomMenuCartItem(); // 항상 새 아이템 생성
            insertCartItem.setRoomMenuCart(roomMenuCart); // 장바구니 연결
            insertCartItem.setRoomMenu(roomMenu); // 메뉴 연결
            insertCartItem.setRoomMenuCartItemAmount(roomMenuCartItemDTO.getRoomMenuCartItemAmount()); // 초기 수량 설정

            // RoomMenuCartItem에 옵션 요약 정보 설정
            insertCartItem.setRoomMenuSelectOptionName(selectedOptionName.toString());
            insertCartItem.setRoomMenuSelectOptionPrice(totalSelectedOptionPrice);

            // 최종 가격 계산 (기본가 + 옵션 가격 합산) * 수량
            int finalItemTotalPrice = (roomMenu.getRoomMenuPrice() + totalSelectedOptionPrice) * roomMenuCartItemDTO.getRoomMenuCartItemAmount();
            insertCartItem.setRoomMenuCartItemPrice(finalItemTotalPrice); // RoomMenuCartItemPrice 필드에 최종 가격 설정


            // 아이템 저장 (RoomMenuCartItem 엔티티를 먼저 저장하여 ID 생성)
            RoomMenuCartItem savedCartItem = roomMenuCartItemRepository.save(insertCartItem);
            log.info("새 장바구니 아이템 저장 완료. ID: {}", savedCartItem.getRoomMenuCartItemNum());

            // RoomMenuCartItemOption 엔티티 저장 (CartItem 저장 후)
            if (optionDTOList != null && !optionDTOList.isEmpty()) {
                log.info("새 아이템에 대한 RoomMenuCartItemOption {}개를 저장합니다.", optionDTOList.size());
                for (RoomMenuCartItemOptionDTO optionDTO : optionDTOList) {
                    // RoomMenuOption을 다시 조회하여 이름/가격 일관성 확보 (또는 DTO 정보 사용)
                    RoomMenuOption menuOption = roomMenuOptionRepository.findById(optionDTO.getRoomMenuCartItemOptionNum())
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionDTO.getRoomMenuCartItemOptionNum()));

                    // 3.  이 옵션이 해당 상품(roomMenu)에 속하는지 검증
                    if (!menuOption.getRoomMenu().getRoomMenuNum().equals(roomMenu.getRoomMenuNum())) {
                        throw new IllegalArgumentException("옵션 '" + menuOption.getRoomMenuOptionName() + "'는 상품 '" + roomMenu.getRoomMenuName() + "'의 옵션이 아닙니다.");
                    }

                    RoomMenuCartItemOption option = new RoomMenuCartItemOption();
                    option.setRoomMenuCartItem(savedCartItem); // 저장된 CartItem 참조 설정
                    option.setRoomMenuCartItemOptionName(menuOption.getRoomMenuOptionName());
                    option.setRoomMenuOption(menuOption); // 메뉴의 옵션 차감을 위해서 필요한 필드
                    option.setRoomMenuCartItemOptionPrice(menuOption.getRoomMenuOptionPrice()); // 옵션 개당 가격
                    option.setRoomMenuCartItemOptionAmount(optionDTO.getRoomMenuCartItemOptionAmount()); // 해당 옵션의 선택 수량

                    roomMenuCartItemOptionRepository.save(option); // 옵션 엔티티 저장
                }
                log.info("RoomMenuCartItemOption 저장 완료.");
            }

            return savedCartItem.getRoomMenuCartItemNum(); // 새로 저장된 아이템의 ID 반환
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
     * @param RoomMenuCartItemNum Long : 수량을 변경할 장바구니 아이템의 ID.
     * @param roomMenuCartItemAmount Integer : 변경할 새로운 수량. (0 이하일 경우 아이템 삭제 고려)
     * @throws EntityNotFoundException : `RoomMenuCartItemNum`에 해당하는 아이템이 존재하지 않는 경우.
     * @throws IllegalArgumentException : 수량이 유효하지 않은 경우 (예: 음수).
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : -
     **************************************************/

    @Override
    public void RoomMenuCartItemAmountUpdate(Long RoomMenuCartItemNum, Integer roomMenuCartItemAmount) {
        log.info("장바구니 수량 업데이트 서비스 진입");
        log.info("아이템의 pk" + RoomMenuCartItemNum);
        log.info("아이템의 수량" + roomMenuCartItemAmount);
        RoomMenuCartItem roomMenuCartItem =
                roomMenuCartItemRepository.findById(RoomMenuCartItemNum).orElseThrow(EntityNotFoundException::new);

        roomMenuCartItem.setRoomMenuCartItemAmount(roomMenuCartItemAmount);

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

}