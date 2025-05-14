package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.dto.RoomMenuOptionDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOption;
import com.sixthsense.hexastay.entity.RoomMenuTranslation;
import com.sixthsense.hexastay.repository.RoomMenuLikeRepository;
import com.sixthsense.hexastay.repository.RoomMenuOptionRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.repository.RoomMenuTranslationRepository;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**************************************************
 * 클래스명 : RoomMenuServiceImpl
 * 기능   : 룸서비스 메뉴 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다.
 * (RoomMenuService 인터페이스 구현)
 * 메뉴 등록(이미지 및 옵션 포함), 목록 조회(검색, 페이지네이션, 다국어 지원), 상세 조회(옵션 및 다국어 지원),
 * 수정(이미지 및 옵션 포함), 삭제 및 관리자별 호텔 객실 목록 조회 등의 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-05-09
 * 주요 메소드/기능 : insert, RoomMenuList, read, modify, delete, getMenusWithLocale,
 * searchRoomList, convertToHotelRoomDto
 **************************************************/

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomMenuServiceImpl implements RoomMenuService {

    private final RoomMenuRepository roomMenuRepository;
    private final RoomMenuTranslationRepository roomMenuTranslationRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final RoomMenuOptionRepository roomMenuOptionRepository;
    private final RoomMenuLikeRepository roomMenuLikeRepository;


    /**************************************************
     * 메소드명 : insert
     * 룸서비스 메뉴 신규 등록 (이미지 및 옵션 포함)
     * 기능: 전달받은 `RoomMenuDTO` 정보를 기반으로 새로운 룸서비스 메뉴를 생성하고 데이터베이스에 저장합니다.
     * 메뉴 정보와 함께 옵션 정보(`RoomMenuOptionDTO` 목록)도 저장하며, 메뉴 대표 이미지가 있는 경우
     * 파일을 지정된 경로에 저장하고 메타데이터를 엔티티에 기록합니다.
     * 다국어 지원 여부에 따라 개발자 승인 상태를 초기 설정합니다.
     * @param roomMenuDTO RoomMenuDTO : 등록할 룸서비스 메뉴의 정보 (이름, 가격, 이미지 파일, 옵션 목록 등)를 담은 DTO.
     * @return RoomMenuDTO : 성공적으로 등록된 룸서비스 메뉴 정보를 담은 DTO.
     * @throws IOException : 이미지 파일 저장 중 오류가 발생한 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-01
     * 수정일 : 2025-04-27
     **************************************************/

    @Override
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO) throws IOException {
        log.info("룸서비스 아이템 등록 서비스 진입" + roomMenuDTO);
        if (roomMenuDTO.getRoomMenuImage() != null && !roomMenuDTO.getRoomMenuImage().isEmpty()) {
            log.info("파일: " + roomMenuDTO.getRoomMenuImage().getOriginalFilename());
        } else {
            log.info("업로드된 이미지가 없습니다.");
        }

        RoomMenu roomMenu = modelMapper.map(roomMenuDTO, RoomMenu.class);
        roomMenuRepository.save(roomMenu); // 이게 먼저 되어야 함!

        // 2. 옵션 저장
        if (roomMenuDTO.getOptions() != null) {
            for (RoomMenuOptionDTO optDTO : roomMenuDTO.getOptions()) {
                RoomMenuOption option = modelMapper.map(optDTO, RoomMenuOption.class);
                option.setRoomMenu(roomMenu);  // 저장된 객체 참조
                roomMenuOptionRepository.save(option);  // 이제 저장 가능!
            }
        }

        // 이미지 파일 처리
        if (roomMenuDTO.getRoomMenuImage() != null && !roomMenuDTO.getRoomMenuImage().isEmpty()) {
            // 1. 파일 이름 생성
            String fileOriginalName = roomMenuDTO.getRoomMenuImage().getOriginalFilename();

            // 만약에 다국어가 활성화 되어 있으면.. 즉, 다국어 체크된 경우에만 승인 필요 일반 등록 시엔 바로 승인 처리
            roomMenu.setApprovedByDev(!roomMenu.getSupportsMultilang());

            if (fileOriginalName != null && fileOriginalName.lastIndexOf(".") > 0) {
                // 2. 상호명_저장된pk
                String fileFirstName = roomMenuDTO.getRoomMenuName() + "_" + roomMenu.getRoomMenuNum();
                // 3. 확장자 추출
                String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                // 4. 파일 이름
                String fileName = fileFirstName + fileSubName;
                // 파일 메타 정보 설정
                roomMenuDTO.setRoomMenuImageMeta("/roommenu/" + fileName);
                log.info("파일" + roomMenuDTO.getRoomMenuImage().getOriginalFilename());

                // 5. 저장할 경로 설정
                Path uploadPath = Paths.get("c:/data/hexastay", "roommenu/" + fileName);
                Path createPath = Paths.get("c:/data/hexastay", "roommenu/");
                // 저장할 디렉토리가 없으면 생성
                if (!Files.exists(createPath)) {
                    Files.createDirectories(createPath);
                }

                // 파일을 저장
                try {
                    roomMenuDTO.getRoomMenuImage().transferTo(uploadPath.toFile());
                } catch (IOException e) {
                    log.error("파일 저장 중 오류 발생", e);
                    throw new IOException("파일 저장 중 오류가 발생했습니다.", e);
                }
            }
        }

        // 파일 메타데이터 저장
        roomMenu.setRoomMenuImageMeta(roomMenuDTO.getRoomMenuImageMeta());

        // 다시 DTO로 변환
        roomMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

        log.info("디티오로 변환된 등록 값" + roomMenuDTO);
        return roomMenuDTO;
    }


    /**************************************************
     * 메소드명 : RoomMenuList
     * 룸서비스 메뉴 목록 조회 (검색, 페이지네이션, 다국어 지원)
     * 기능: 다양한 검색 조건(`type`, `keyword`, `category`), 페이지네이션 정보(`pageable`),
     * 현재 로케일(`locale`) 및 사용자 뷰 여부(`forUserView`)를 기반으로 룸서비스 메뉴 목록을 조회합니다.
     * 조회된 각 메뉴는 현재 로케일에 따라 번역된 정보(이름, 내용, 카테고리)와 재고 상태("품절"/"판매중")를
     * 포함하여 `RoomMenuDTO`로 변환된 후, `Page<RoomMenuDTO>` 형태로 반환됩니다.
     * @param pageable Pageable : 페이징 처리 정보 (페이지 번호, 크기, 정렬 등).
     * @param type String : 검색 유형 (C: 카테고리, S: 이름, P: 가격 이하, A: 재고 이상, N: 이름 또는 가격, L: 좋아요 순, PL: 가격 낮은순, PH: 가격 높은순).
     * @param keyword String : 검색어 (검색 유형에 따라 사용됨).
     * @param category String : 검색할 카테고리명.
     * @param locale Locale : 다국어 처리를 위한 현재 로케일 정보.
     * @param forUserView boolean : 사용자용 뷰인지 여부 (필터링 조건에 사용됨).
     * @return Page<RoomMenuDTO> : 조회 및 변환된 룸서비스 메뉴 DTO 페이지 객체.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-03
     * 수정일 : 2025-04-18
     **************************************************/

    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword,
                                          String category, Locale locale, boolean forUserView) {

        Page<RoomMenu> roomMenuPage;
        String lang = locale.getLanguage();

        // 카테고리가 존재한다면 ?
        boolean hasCategory = category != null && !category.trim().isEmpty();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        // === 검색 조건 처리 === //
        if ("C".equals(type) && category != null && !category.trim().isEmpty()) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                roomMenuPage = roomMenuRepository.searchByCategoryAndNameForUser(category, keyword, pageable);
            } else {
                roomMenuPage = roomMenuRepository.searchByCategoryForUser(category, pageable);
            }
        } else if ("S".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            roomMenuPage = roomMenuRepository.searchByNameForUser(keyword, pageable);
        } else if ("P".equals(type) && hasKeyword) {
            try {
                int price = Integer.parseInt(keyword);
                roomMenuPage = hasCategory
                        ? roomMenuRepository.findByRoomMenuCategoryAndRoomMenuPriceLessThanEqual(category, price, pageable)
                        : roomMenuRepository.findByRoomMenuPriceLessThanEqual(price, pageable);
            } catch (NumberFormatException e) {
                roomMenuPage = roomMenuRepository.findAll(pageable);
            }
        } else if ("A".equals(type) && hasKeyword) {
            try {
                int amount = Integer.parseInt(keyword);
                roomMenuPage = hasCategory
                        ? roomMenuRepository.findByRoomMenuCategoryAndRoomMenuAmountGreaterThan(category, amount, pageable)
                        : roomMenuRepository.findByRoomMenuAmountGreaterThan(amount, pageable);
            } catch (NumberFormatException e) {
                roomMenuPage = roomMenuRepository.findAll(pageable);
            }
        } else if ("N".equals(type) && hasKeyword) {
            try {
                int price = Integer.parseInt(keyword);
                roomMenuPage = hasCategory
                        ? roomMenuRepository.findByRoomMenuCategoryAndRoomMenuNameContainingOrRoomMenuPriceLessThanEqual(category, keyword, price, pageable)
                        : roomMenuRepository.findByRoomMenuNameContainingOrRoomMenuPriceLessThanEqual(keyword, price, pageable);
            } catch (NumberFormatException e) {
                roomMenuPage = hasCategory
                        ? roomMenuRepository.findByRoomMenuCategoryAndRoomMenuNameContaining(category, keyword, pageable)
                        : roomMenuRepository.findByRoomMenuNameContaining(keyword, pageable);
            }
        } else if ("L".equals(type)) {
            roomMenuPage = roomMenuRepository.findAllOrderByLikeCountDescForUser(pageable);
        } else if ("PL".equals(type)) {
            roomMenuPage = roomMenuRepository.findBySupportsMultilangFalseOrApprovedByDevTrueOrderByRoomMenuPriceAsc(pageable);
        } else if ("PH".equals(type)) {
            roomMenuPage = roomMenuRepository.findBySupportsMultilangFalseOrApprovedByDevTrueOrderByRoomMenuPriceDesc(pageable);
        }else {
            roomMenuPage = roomMenuRepository.findBySupportsMultilangFalseOrApprovedByDevTrue(pageable);
        }

        // === DTO 변환 + 다국어 및 상태 처리 === //
        List<RoomMenuDTO> dtoList = roomMenuPage.getContent().stream()
                .map(menu -> {
                    RoomMenuDTO dto = modelMapper.map(menu, RoomMenuDTO.class);
                    dto.setRoomMenuStatus(menu.getRoomMenuAmount() <= 0 ? "품절" : "판매중");

                    roomMenuTranslationRepository
                            .findByRoomMenu_RoomMenuNumAndLocale(menu.getRoomMenuNum(), lang)
                            .ifPresent(translation -> {
                                dto.setRoomMenuName(translation.getRoomMenuTransLationName());
                                dto.setRoomMenuContent(translation.getRoomMenuTransLationContent());
                                dto.setRoomMenuCategory(translation.getRoomMenuTransLationCategory());
                            });

                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, roomMenuPage.getTotalElements());
    }


    /**************************************************
     * 메소드명 : read
     * 룸서비스 메뉴 상세 정보 조회 (옵션 및 다국어 지원)
     * 기능: 특정 룸서비스 메뉴 번호(`num`)를 기준으로 메뉴의 상세 정보를 조회합니다.
     * 현재 로케일(`locale`)에 맞는 번역된 메뉴 이름과 내용을 적용하고,
     * 해당 메뉴에 속한 모든 옵션 목록을 함께 조회하여 `RoomMenuDTO`로 구성하여 반환합니다.
     * @param num Long : 조회할 룸서비스 메뉴의 ID.
     * @param locale Locale : 다국어 처리를 위한 현재 로케일 정보.
     * @return RoomMenuDTO : 조회된 룸서비스 메뉴의 상세 정보 DTO (번역 및 옵션 포함).
     * @throws EntityNotFoundException : `num`에 해당하는 메뉴가 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-04-27
     **************************************************/

    @Override
    public RoomMenuDTO read(Long num, Locale locale) {
        log.info("상세보기 페이지 서비스 진입" + num);

        RoomMenu roomMenu = roomMenuRepository.findByRoomMenuNum(num);
        RoomMenuDTO roomMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

        // 번역 처리
        Optional<RoomMenuTranslation> translation = roomMenuTranslationRepository
                .findByRoomMenu_RoomMenuNumAndLocale(roomMenu.getRoomMenuNum(), locale.getLanguage());

        if (translation.isPresent()) {
            roomMenuDTO.setRoomMenuName(translation.get().getRoomMenuTransLationName());
            roomMenuDTO.setRoomMenuContent(translation.get().getRoomMenuTransLationContent());
        }

        List<RoomMenuOption> options = roomMenuOptionRepository.findByRoomMenu(roomMenu);
        List<RoomMenuOptionDTO> optionDTOs = options.stream()
                .map(option -> modelMapper.map(option, RoomMenuOptionDTO.class))
                .collect(Collectors.toList());

        roomMenuDTO.setOptions(optionDTOs);

        return roomMenuDTO;

    }

    /**************************************************
     * 메소드명 : modify
     * 룸서비스 메뉴 정보 수정 (이미지 및 옵션 포함)
     * 기능: 전달받은 `RoomMenuDTO` 정보를 사용하여 기존 룸서비스 메뉴를 수정합니다.
     * 메뉴 기본 정보, 옵션 목록 (추가/수정/삭제), 대표 이미지를 업데이트할 수 있습니다.
     * 새로운 이미지가 제공되면 기존 이미지는 파일 시스템에서 삭제되고 새 이미지로 대체됩니다.
     * @param roomMenuDTO RoomMenuDTO : 수정할 룸서비스 메뉴의 정보 (메뉴 ID, 변경할 내용, 새 이미지 파일, 옵션 목록 등).
     * @return RoomMenuDTO : 성공적으로 수정된 룸서비스 메뉴 정보를 담은 DTO.
     * @throws EntityNotFoundException : 수정 대상 룸메뉴 또는 전달된 옵션 ID에 해당하는 옵션이 존재하지 않는 경우.
     * @throws RuntimeException : 이미지 파일 처리(디렉토리 생성, 파일 저장) 중 오류가 발생하거나 데이터 수정 중 예외가 발생한 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-04-27
     **************************************************/

    @Override
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO) {
        log.info("업데이트 서비스 진입: " + roomMenuDTO);

        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuDTO.getRoomMenuNum())
                .orElseThrow(() -> new EntityNotFoundException("해당 룸메뉴가 존재하지 않습니다."));

        // 옵션 처리
        List<RoomMenuOptionDTO> optionDTOs = roomMenuDTO.getOptions();
        List<RoomMenuOption> currentOptions = roomMenuOptionRepository.findByRoomMenu(roomMenu);

        // 기존 옵션 ID들 모으기
        Set<Long> submittedIds = optionDTOs.stream()
                .map(RoomMenuOptionDTO::getRoomMenuOptionNum)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        // 기존 옵션 중 삭제 대상 제거
        for (RoomMenuOption existingOption : currentOptions) {
            if (!submittedIds.contains(existingOption.getRoomMenuOptionNum())) {
                roomMenuOptionRepository.delete(existingOption);
            }
        }

        // 새로 추가되거나 수정된 옵션 저장
        for (RoomMenuOptionDTO dto : optionDTOs) {
            RoomMenuOption option;
            if (dto.getRoomMenuOptionNum() != null && dto.getRoomMenuOptionNum() > 0) {
                option = roomMenuOptionRepository.findById(dto.getRoomMenuOptionNum())
                        .orElseThrow(() -> new RuntimeException("옵션 없음"));
            } else {
                option = new RoomMenuOption();
                option.setRoomMenu(roomMenu);
            }
            option.setRoomMenuOptionName(dto.getRoomMenuOptionName());
            option.setRoomMenuOptionPrice(dto.getRoomMenuOptionPrice());
            option.setRoomMenuOptionAmount(dto.getRoomMenuOptionAmount()); //
            roomMenuOptionRepository.save(option);
        }

        try {

            roomMenu.setRoomMenuName(roomMenuDTO.getRoomMenuName());
            roomMenu.setRoomMenuPrice(roomMenuDTO.getRoomMenuPrice());
            roomMenu.setRoomMenuAmount(roomMenuDTO.getRoomMenuAmount());
            roomMenu.setRoomMenuCategory(roomMenuDTO.getRoomMenuCategory());
            roomMenu.setRoomMenuStatus(roomMenuDTO.getRoomMenuStatus());
            roomMenu.setRoomMenuContent(roomMenuDTO.getRoomMenuContent());
            MultipartFile newImageFile = roomMenuDTO.getRoomMenuImage();
            if (newImageFile != null && !newImageFile.isEmpty()) {

                // 기존 이미지 메타 정보
                String oldImageMeta = roomMenu.getRoomMenuImageMeta();

                // 기존 파일 삭제
                if (oldImageMeta != null && !oldImageMeta.isEmpty()) {
                    Path oldFilePath = Paths.get("c:/data/hexastay", oldImageMeta);
                    File oldFile = oldFilePath.toFile();
                    if (oldFile.exists()) {
                        oldFile.delete();
                        log.info("기존 이미지 삭제됨: " + oldFilePath);
                    }
                }

                // 새 파일 이름 생성
                String fileOriginalName = newImageFile.getOriginalFilename();
                if (fileOriginalName != null && fileOriginalName.lastIndexOf(".") > 0) {
                    String fileFirstName = roomMenuDTO.getRoomMenuName() + "_" + roomMenu.getRoomMenuNum();
                    String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                    String fileName = fileFirstName + fileSubName;

                    // 저장 경로
                    Path saveDirPath = Paths.get("c:/data/hexastay", "roommenu/");
                    Path saveFilePath = saveDirPath.resolve(fileName);

                    // 디렉토리가 없으면 생성
                    if (!Files.exists(saveDirPath)) {
                        try {
                            Files.createDirectories(saveDirPath);
                        } catch (IOException e) {
                            log.error("디렉토리 생성 오류", e);
                            throw new RuntimeException("디렉토리 생성 실패", e);
                        }
                    }

                    // 파일 저장
                    try {
                        newImageFile.transferTo(saveFilePath.toFile());
                    } catch (IOException e) {
                        log.error("파일 저장 중 오류 발생", e);
                        throw new RuntimeException("파일 저장 실패", e);
                    }

                    // 이미지 메타정보 갱신
                    String metaPath = "/roommenu/" + fileName;
                    roomMenu.setRoomMenuImageMeta(metaPath);
                    roomMenuDTO.setRoomMenuImageMeta(metaPath);
                }
            }

            // DB 저장
            RoomMenu updated = roomMenuRepository.save(roomMenu);

            return modelMapper.map(updated, RoomMenuDTO.class);

        } catch (Exception e) {
            log.error("데이터 수정 실패: " + e.getMessage(), e);
            throw new RuntimeException("데이터 수정에 실패했습니다.", e);
        }
    }


    /**************************************************
     * 메소드명 : delete
     * 룸서비스 메뉴 삭제
     * 기능: 특정 룸서비스 메뉴 번호(`num`)에 해당하는 메뉴를 데이터베이스에서 삭제합니다.
     * 해당 메뉴와 관련된 좋아요 정보(`RoomMenuLike`)도 함께 삭제됩니다.
     * (주의: 연결된 옵션, 번역, 주문 항목 등의 삭제 정책은 이 메소드에서 직접 처리하지 않으므로,
     * DB 제약조건 또는 CascadeType 설정에 따라 동작하거나 별도 처리가 필요할 수 있습니다.)
     * @param num Long : 삭제할 룸서비스 메뉴의 ID.
     * @throws EntityNotFoundException : 삭제할 메뉴 ID에 해당하는 RoomMenu가 없는 경우 (deleteById는 예외를 발생시키지 않으므로, 사전 조회 또는 다른 방식 고려)
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : -
     **************************************************/

    @Override
    public void delete(Long num) {

        log.info("삭제 서비스 진입" + num);
        roomMenuLikeRepository.deleteByRoomMenu_RoomMenuNum(num);
        roomMenuRepository.deleteById(num);
        log.info("삭제완료 db를 확인하세요.");

    }

    // todo : 오류날 수 있으니 주의 삭제 하지마
    /* *************************************************
     * 메소드명 : getMenusWithLocale
     * 특정 로케일 기준 전체 메뉴 목록 조회 (다국어 적용)
     * 기능: 모든 룸서비스 메뉴를 조회한 후, 각 메뉴에 대해 주어진 로케일 문자열(`locale`)에 해당하는
     * 번역 정보(이름, 내용, 카테고리)를 적용하여 `RoomMenuDTO` 목록으로 변환하여 반환합니다.
     * 번역 정보가 없으면 원본 메뉴 정보를 사용합니다.
     * @param locale String : 적용할 로케일 코드 (예: "ko", "en").
     * @return List<RoomMenuDTO> : 번역이 적용된 전체 룸서비스 메뉴 DTO 목록.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-17
     * 수정일 : -
     **************************************************/

    // 번역
    @Override
    public List<RoomMenuDTO> getMenusWithLocale(String locale) {
        List<RoomMenu> menus = roomMenuRepository.findAll();

        return menus.stream()
                .map(menu -> {
                    RoomMenuDTO dto = new RoomMenuDTO();
                    dto.setRoomMenuNum(menu.getRoomMenuNum());

                    // 기본값 (예: 한국어)
                    dto.setRoomMenuName(menu.getRoomMenuName());
                    dto.setRoomMenuContent(menu.getRoomMenuContent());
                    dto.setRoomMenuCategory(menu.getRoomMenuCategory());

                    // 로케일별로 덮어쓰기
                    roomMenuTranslationRepository.findByRoomMenu_RoomMenuNumAndLocale(menu.getRoomMenuNum(), locale)
                            .ifPresent(translation -> {
                                dto.setRoomMenuName(translation.getRoomMenuTransLationName()); // ← 영어가 여기로 들어옴!
                                dto.setRoomMenuContent(translation.getRoomMenuTransLationContent());
                                dto.setRoomMenuCategory(translation.getRoomMenuTransLationCategory());
                            });

                    dto.setRoomMenuPrice(menu.getRoomMenuPrice());
                    dto.setRoomMenuImageMeta(menu.getRoomMenuImageMeta());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // todo(10) : 중복된 매소드, 위에 통합으로 합침. 일단 오류생기니까 맵둬
    /**************************************************
     * 메소드명 : searchRoomMenuList
     * (중복 가능성 있음) 룸서비스 메뉴 목록 검색 및 조회 (페이지네이션, 다국어)
     * 기능: `RoomMenuList` 메소드와 유사하게 검색 조건, 페이지네이션, 로케일을 기반으로
     * 룸서비스 메뉴 목록을 조회하고 `Page<RoomMenuDTO>`로 반환합니다.
     * (주의: `RoomMenuList` 메소드와 기능 및 시그니처가 거의 동일하여 중복될 수 있습니다.)
     * @param pageable Pageable : 페이징 정보.
     * @param type String : 검색 유형.
     * @param keyword String : 검색어.
     * @param category String : 검색할 카테고리.
     * @param locale Locale : 다국어 처리를 위한 로케일.
     * @return Page<RoomMenuDTO> : 조회된 룸서비스 메뉴 DTO 페이지 객체.
     * 작성자 : 김윤겸
     * 등록일 : -
     * 수정일 : -
     **************************************************/

    @Override
    public Page<RoomMenuDTO> searchRoomMenuList(Pageable pageable, String type, String keyword, String category, Locale locale) {
        log.info("룸서비스 상품 리스트 서비스 진입");

        Page<RoomMenu> roomMenuPage;
        String lang = locale.getLanguage();
        log.info("현재 언어: " + lang);

        // 카테고리 선택 시 검색
        if ("C".equals(type) && category != null && !category.trim().isEmpty()) {
            roomMenuPage = roomMenuRepository.findByRoomMenuCategory(category, pageable);
        } else if ("S".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            // 이름 검색
            roomMenuPage = roomMenuRepository.findByRoomMenuNameContaining(keyword, pageable);
        } else if ("P".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            // 가격 검색
            try {
                int price = Integer.parseInt(keyword);  // 가격을 숫자로 변환
                roomMenuPage = roomMenuRepository.findByRoomMenuPriceLessThanEqual(price, pageable);  // 가격보다 큰 값 검색
            } catch (NumberFormatException e) {
                // 숫자가 아닌 값을 입력한 경우, 전체 검색
                roomMenuPage = roomMenuRepository.findAll(pageable);
            }
        } else if ("A".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            // 재고량 검색
            try {
                int amount = Integer.parseInt(keyword);  // 재고량을 숫자로 변환
                roomMenuPage = roomMenuRepository.findByRoomMenuAmountGreaterThan(amount, pageable);  // 재고량보다 큰 값 검색
            } catch (NumberFormatException e) {
                // 잘못된 입력 처리, 전체 검색
                roomMenuPage = roomMenuRepository.findAll(pageable);
            }
        } else if ("N".equals(type) && keyword != null && !keyword.trim().isEmpty()) {
            // 이름 + 가격 검색
            try {
                int price = Integer.parseInt(keyword);
                roomMenuPage = roomMenuRepository.findByRoomMenuNameContainingOrRoomMenuPriceLessThanEqual(keyword, price, pageable);
            } catch (NumberFormatException e) {
                // 가격이 아니라면 이름만으로 검색
                roomMenuPage = roomMenuRepository.findByRoomMenuNameContaining(keyword, pageable);
            }
        } else {
            // 기본 전체 검색
            roomMenuPage = roomMenuRepository.findAll(pageable);
        }

        // DTO로 변환 및 다국어 적용
        Page<RoomMenuDTO> roomMenuDTOList = roomMenuPage.map(roomMenu -> {
            RoomMenuDTO dto = modelMapper.map(roomMenu, RoomMenuDTO.class);

            // 다국어 번역 적용
            roomMenuTranslationRepository
                    .findByRoomMenu_RoomMenuNumAndLocale(roomMenu.getRoomMenuNum(), lang)
                    .ifPresent(translation -> {
                        log.info("번역된 이름: {}", dto.getRoomMenuName());
                        dto.setRoomMenuName(translation.getRoomMenuTransLationName());
                        dto.setRoomMenuContent(translation.getRoomMenuTransLationContent());
                        dto.setRoomMenuCategory(translation.getRoomMenuTransLationCategory());

                        log.info("번역 적용됨 - name: {}, locale: {}", translation.getRoomMenuTransLationName(), lang);
                        log.info("현재 언어: {}", locale.getLanguage());
                    });

            return dto;
        });
        return roomMenuDTOList;
    }

}


