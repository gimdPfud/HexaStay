package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuOptionDTO;
import com.sixthsense.hexastay.service.RoomMenuOptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roommenu/insert/option")
@RequiredArgsConstructor
@Log4j2

public class RoomMenuOptionController {

    private final RoomMenuOptionService roomMenuOptionService;

    // 특정 RoomMenu의 옵션 목록 조회
    @GetMapping("/list/{roomMenuNum}")
    public ResponseEntity<List<RoomMenuOptionDTO>> getOptionList(@PathVariable Long roomMenuNum) {
        List<RoomMenuOptionDTO> options = roomMenuOptionService.roomMenuOptionAllList(roomMenuNum);
        return ResponseEntity.ok(options);
    }

    // 특정 RoomMenu에 옵션 생성
    // URL 예: POST /roommenu/insert/option/create/1
    @PostMapping("/create/{roomMenuNum}")
    public ResponseEntity<RoomMenuOptionDTO> createOption(@PathVariable Long roomMenuNum,
                                                          @RequestBody RoomMenuOptionDTO roomMenuOptionDTO) {
        RoomMenuOptionDTO createdOption = roomMenuOptionService.roomMenuOptionInsert(roomMenuNum, roomMenuOptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    // 옵션 수정
    @PutMapping("/update/{roomMenuOptionNum}")
    public ResponseEntity<RoomMenuOptionDTO> updateOption(@PathVariable Long roomMenuOptionNum,
                                                          @RequestBody RoomMenuOptionDTO roomMenuOptionDTO) {
        RoomMenuOptionDTO updatedOption = roomMenuOptionService.roomMenuOptionUpdate(roomMenuOptionNum, roomMenuOptionDTO);
        return ResponseEntity.ok(updatedOption);
    }

    // 옵션 삭제
    @DeleteMapping("/delete/{roomMenuOptionNum}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long roomMenuOptionNum) {
        roomMenuOptionService.roomMenuOptionDelete(roomMenuOptionNum);
        return ResponseEntity.noContent().build();
    }

}
