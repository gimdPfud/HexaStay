package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomMenuServiceImpl implements RoomMenuService {

    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    /**************************************************
     * 룸서비스 메뉴 등록
     * 기능 : 룸서비스 메뉴를 등록하는 서비스
     * 설명 : 전달된 RoomMenuDTO를 엔티티로 변환하여 DB에 저장하고,
     *        다시 DTO로 변환하여 클라이언트에게 반환
     **************************************************/

    @Override
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO) throws IOException {
        log.info("룸서비스 아이템 등록 서비스 진입" + roomMenuDTO);

        // 모델맵퍼로 dto 변환
        RoomMenu roomMenu = modelMapper.map(roomMenuDTO, RoomMenu.class);

        roomMenu = roomMenuRepository.save(roomMenu);

        // 이미지 파일 처리
        if (roomMenuDTO.getRoomMenuImage() != null && !roomMenuDTO.getRoomMenuImage().isEmpty()) {
            // 1. 파일 이름 생성
            String fileOriginalName = roomMenuDTO.getRoomMenuImage().getOriginalFilename();

            if (fileOriginalName != null && fileOriginalName.lastIndexOf(".") > 0) {
                // 2. 상호명_저장된pk
                String fileFirstName = roomMenuDTO.getRoomMenuName() + "_" + roomMenu.getRoomMenuNum();
                // 3. 확장자 추출
                String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
                // 4. 파일 이름
                String fileName = fileFirstName + fileSubName;

                // 파일 메타 정보 설정
                roomMenuDTO.setRoomMenuImageMeta("/roommenu/" + fileName);

                // 5. 저장할 경로 설정
                Path uploadPath = Paths.get("path/to/store", "roommenu", fileName);  // 시스템에 맞는 경로로 변경
                Path createPath = Paths.get("path/to/store", "roommenu");

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

        // 엔티티 업데이트 (두 번째 save 불필요, 첫 번째 저장으로 충분)
//        roomMenu = roomMenuRepository.save(roomMenu);

        // 다시 DTO로 변환
        roomMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

        log.info("디티오로 변환된 등록 값" + roomMenuDTO);
        return roomMenuDTO;
    }


    /**************************************************
     * 룸서비스 메뉴 리스트 조회와 검색
     * 기능 : 룸서비스 메뉴의 목록을 페이지네이션 처리하여 반환
     * 설명 : Pageable을 사용하여 페이지 단위로 메뉴 리스트를 조회하고,
     *        해당 리스트를 DTO로 변환하여 반환
     *        수정일자 : 2025-04-07
     **************************************************/

    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category) {
        log.info("룸서비스 상품 리스트 서비스 진입");

        Page<RoomMenu> roomMenuPage;

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
                roomMenuPage = roomMenuRepository.findByRoomMenuPriceGreaterThan(price, pageable);  // 가격보다 큰 값 검색
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
                roomMenuPage = roomMenuRepository.findByRoomMenuNameContainingOrRoomMenuPriceGreaterThan(keyword, price, pageable);
            } catch (NumberFormatException e) {
                // 가격이 아니라면 이름만으로 검색
                roomMenuPage = roomMenuRepository.findByRoomMenuNameContaining(keyword, pageable);
            }
        } else {
            // 기본 전체 검색
            roomMenuPage = roomMenuRepository.findAll(pageable);
        }

        // DTO로 변환
        Page<RoomMenuDTO> roomMenuDTOList = roomMenuPage.map(roomMenu -> modelMapper.map(roomMenu, RoomMenuDTO.class));
        return roomMenuDTOList;
    }



    /**************************************************
     * 룸서비스 메뉴 상세 보기
     * 기능 : 특정 메뉴의 상세 정보를 조회
     * 설명 : 메뉴 번호를 이용해 DB에서 해당 메뉴를 조회하고,
     *        이를 DTO로 변환하여 반환
     **************************************************/

    @Override
    public RoomMenuDTO read(Long num) {
        log.info("상세보기 페이지 서비스 진입" + num);

        Optional<RoomMenu> optionalRoomMenu =
                roomMenuRepository.findById(num);
        // inter를 long으로 형 변환

        RoomMenuDTO menuDTO = modelMapper.map(optionalRoomMenu, RoomMenuDTO.class);
        log.info("변환된 dto read service의 값" + menuDTO);

        return menuDTO;

    }

    /**************************************************
     * 룸서비스 메뉴 수정
     * 기능 : 룸서비스 메뉴 정보를 수정
     * 설명 : 전달된 RoomMenuDTO를 엔티티로 변환하고, 해당 메뉴를 수정한 후,
     *        수정된 엔티티를 다시 DTO로 변환하여 반환
     **************************************************/

    @Override
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO) {
        log.info("업데이트 서비스 진입: " + roomMenuDTO);

        try {
            RoomMenu roomMenu = modelMapper.map(roomMenuDTO, RoomMenu.class);

            roomMenu = roomMenuRepository.save(roomMenu);

            RoomMenuDTO updatedMenuDTO = modelMapper.map(roomMenu, RoomMenuDTO.class);

            return updatedMenuDTO;

        } catch (Exception e) {
            log.error("데이터 수정에 실패함: " + e.getMessage(), e);  //
            throw new RuntimeException("데이터 수정에 실패했습니다.", e);
        }
    }


    /**************************************************
     * 룸서비스 메뉴 삭제
     * 기능 : 특정 메뉴를 삭제하는 서비스
     * 설명 : 메뉴 번호로 해당 메뉴를 찾아 삭제
     **************************************************/

    @Override
    public void delete(Long num) {

        log.info("삭제 서비스 진입" + num);
        roomMenuRepository.deleteById(num);

        log.info("삭제완료 db를 확인하세요.");

    }

}
