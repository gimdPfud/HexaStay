package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.dto.RoomMenuOptionDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOption;
import com.sixthsense.hexastay.repository.RoomMenuOptionRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuOptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuOptionServiceImpl implements RoomMenuOptionService {

    private final RoomMenuOptionRepository optionRepository;
    private final RoomMenuRepository roomMenuRepository;
    private final ModelMapper modelMapper;

    /***************************************************
     *
     * 메소드명 : roomMenuOptionAllList
     * 기능 : 특정 룸 메뉴(상품)에 속한 모든 옵션 목록을 조회한다.
     * - 주어진 룸 메뉴 ID로 룸 메뉴 엔티티를 조회한다.
     * - 해당 룸 메뉴 엔티티에 연결된 모든 RoomMenuOption 엔티티들을 조회한다.
     * - 조회된 옵션 엔티티 리스트를 RoomMenuOptionDTO 리스트로 변환하여 반환한다.
     * 매개변수 : Long roomMenuNum - 옵션 목록을 조회할 대상 룸 메뉴의 고유 ID (PK)
     * 반환값 : List<RoomMenuOptionDTO> - 조회된 옵션 정보 DTO들의 리스트. 해당하는 룸 메뉴가 없으면 예외 발생.
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-25
     * 수정일 : -
     *
     ****************************************************/

    // 리스트 불러오기
    @Override
    public List<RoomMenuOptionDTO> roomMenuOptionAllList(Long roomMenuNum) {
        log.info("옵션 리스트 보기 서비스 진입");

        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new RuntimeException("해당하는 룸 메뉴의 아이템을 찾을 수 없음: " + roomMenuNum));

        List<RoomMenuOption> options = optionRepository.findByRoomMenu(roomMenu);

        return options.stream()
                .map(option -> modelMapper.map(option, RoomMenuOptionDTO.class))
                .collect(Collectors.toList());
    }

    /***************************************************
     *
     * 메소드명 : roomMenuOptionInsert
     * 기능 : 특정 룸 메뉴(상품)에 새로운 옵션을 추가(등록)한다.
     * - 주어진 룸 메뉴 ID로 룸 메뉴 엔티티를 조회한다.
     * - 전달받은 RoomMenuOptionDTO를 RoomMenuOption 엔티티로 변환한다.
     * - 변환된 옵션 엔티티에 조회된 룸 메뉴 엔티티를 연결(설정)한다.
     * - 옵션 엔티티를 데이터베이스에 저장한다.
     * - 저장된 옵션 엔티티를 다시 DTO로 변환하여 반환한다.
     * 매개변수 : Long roomMenuNum - 옵션을 추가할 대상 룸 메뉴의 고유 ID (PK)
     * RoomMenuOptionDTO roomMenuOptionDTO - 추가할 옵션의 정보(이름, 가격, 재고량 등)를 담은 DTO
     * 반환값 : RoomMenuOptionDTO - 데이터베이스에 성공적으로 저장된 옵션 정보를 담은 DTO. 해당하는 룸 메뉴가 없으면 예외 발생.
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-25
     * 수정일 : -
     *
     ****************************************************/

    @Override
    public RoomMenuOptionDTO roomMenuOptionInsert(Long roomMenuNum, RoomMenuOptionDTO roomMenuOptionDTO) {
        log.info("옵션 생성 서비스 진입");

        RoomMenu roomMenu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new RuntimeException("해당하는 룸 메뉴의 아이템을 찾을 수 없음: " + roomMenuNum));

        RoomMenuOption option = modelMapper.map(roomMenuOptionDTO, RoomMenuOption.class);
        option.setRoomMenu(roomMenu);
        option = optionRepository.save(option);
        return modelMapper.map(option, RoomMenuOptionDTO.class);
    }

    /***************************************************
     *
     * 메소드명 : roomMenuOptionUpdate
     * 기능 : 기존 룸 메뉴 옵션의 정보를 수정한다.
     * - 주어진 옵션 ID로 RoomMenuOption 엔티티를 조회한다.
     * - 전달받은 RoomMenuOptionDTO의 정보로 조회된 엔티티의 필드(이름, 가격, 재고량)를 업데이트한다.
     * - 업데이트된 엔티티를 데이터베이스에 저장한다. (JPA 더티 체킹 또는 명시적 save)
     * - 업데이트된 엔티티를 다시 DTO로 변환하여 반환한다.
     * 매개변수 : Long roomMenuOptionNum - 수정할 옵션의 고유 ID (PK)
     * RoomMenuOptionDTO roomMenuOptionDTO - 수정할 옵션의 새로운 정보를 담은 DTO
     * 반환값 : RoomMenuOptionDTO - 데이터베이스에 성공적으로 수정된 옵션 정보를 담은 DTO. 해당하는 옵션이 없으면 예외 발생.
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-25
     * 수정일 : -
     *
     ****************************************************/

    @Override
    public RoomMenuOptionDTO roomMenuOptionUpdate(Long roomMenuOptionNum, RoomMenuOptionDTO roomMenuOptionDTO) {
        log.info("옵션 업데이트 서비스 진입");

        RoomMenuOption option = optionRepository.findById(roomMenuOptionNum)
                .orElseThrow(() -> new RuntimeException("해당하는 옵션 값의 pk를 찾을 수 없음: " + roomMenuOptionNum));

        option.setRoomMenuOptionName(roomMenuOptionDTO.getRoomMenuOptionName());
        option.setRoomMenuOptionPrice(roomMenuOptionDTO.getRoomMenuOptionPrice());
        option.setRoomMenuOptionAmount(roomMenuOptionDTO.getRoomMenuOptionAmount()); // 추가
        optionRepository.save(option);
        return modelMapper.map(option, RoomMenuOptionDTO.class);
    }

    /***************************************************
     *
     * 메소드명 : roomMenuOptionDelete
     * 기능 : 특정 룸 메뉴 옵션을 데이터베이스에서 삭제한다.
     * - 주어진 옵션 ID로 RoomMenuOption 엔티티를 조회한다.
     * - 조회된 엔티티를 데이터베이스에서 삭제한다.
     * 매개변수 : Long roomMenuOptionNum - 삭제할 옵션의 고유 ID (PK)
     * 반환값 : void - 작업 성공 시 반환값 없음. 해당하는 옵션이 없으면 예외 발생.
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-25
     * 수정일 : -
     *
     ****************************************************/

    @Override
    public void roomMenuOptionDelete(Long roomMenuOptionNum) {
        log.info("옵션 삭제 서비스 진입");

        RoomMenuOption option = optionRepository.findById(roomMenuOptionNum)
                .orElseThrow(() -> new RuntimeException("삭제할 pk 값을 찾을 수 없음: " + roomMenuOptionNum));
        optionRepository.delete(option);

    }

    /***************************************************
     *
     * 메소드명 : hasOption
     * 기능 : 특정 룸 메뉴(상품)에 연결된 옵션이 하나라도 존재하는지 여부를 확인한다.
     * 주로 상품 삭제 전 등에 옵션 존재 유무를 확인하기 위해 사용될 수 있다.
     * 매개변수 : Long roomMenuNum - 옵션 존재 여부를 확인할 대상 룸 메뉴의 고유 ID (PK)
     * 반환값 : boolean - 해당 룸 메뉴에 옵션이 하나 이상 존재하면 true, 없으면 false 반환.
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-25
     * 수정일 : -
     *
     ****************************************************/

    // admin/list에서 삭제 시 옵션 확인하기.
    @Override
    public boolean hasOption(Long roomMenuNum) {
        log.info("옵션 존재여부 서비스 진입" + roomMenuNum);
            return optionRepository.existsByRoomMenu_RoomMenuNum(roomMenuNum);
        }
    }

