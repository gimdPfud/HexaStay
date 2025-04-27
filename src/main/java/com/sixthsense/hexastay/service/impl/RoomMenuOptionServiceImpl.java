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

    @Override
    public RoomMenuOptionDTO roomMenuOptionUpdate(Long roomMenuOptionNum, RoomMenuOptionDTO roomMenuOptionDTO) {
        log.info("옵션 업데이트 서비스 진입");

        RoomMenuOption option = optionRepository.findById(roomMenuOptionNum)
                .orElseThrow(() -> new RuntimeException("해당하는 옵션 값의 pk를 찾을 수 없음: " + roomMenuOptionNum));

        option.setRoomMenuOptionName(roomMenuOptionDTO.getRoomMenuOptionName());
        option.setRoomMenuOptionPrice(roomMenuOptionDTO.getRoomMenuOptionPrice());
        option.setRoomMenuOptionStock(roomMenuOptionDTO.getRoomMenuOptionStock()); // 추가
        optionRepository.save(option);
        return modelMapper.map(option, RoomMenuOptionDTO.class);
    }

    @Override
    public void roomMenuOptionDelete(Long roomMenuOptionNum) {
        log.info("옵션 삭제 서비스 진입");

        RoomMenuOption option = optionRepository.findById(roomMenuOptionNum)
                .orElseThrow(() -> new RuntimeException("삭제할 pk 값을 찾을 수 없음: " + roomMenuOptionNum));
        optionRepository.delete(option);

    }
}
