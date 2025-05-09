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

/**************************************************
 * 클래스명 : RoomMenuOptionController
 * 기능   : 룸서비스 메뉴의 옵션 관련 CRUD 작업을 처리하는 REST 컨트롤러입니다.
 * 특정 룸 메뉴에 대한 옵션 목록 조회, 생성, 수정, 삭제 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-25
 * 수정일 : 2025-05-09
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

public class RoomMenuOptionController {

    private final RoomMenuOptionService roomMenuOptionService;


    /**************************************************
     * 메소드명 : getOptionList
     * 룸 메뉴 옵션 목록 조회
     * 기능: 특정 룸서비스 메뉴(roomMenuNum)에 연관된 모든 옵션 목록을 조회하여 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-25
     * 수정일 : -
     **************************************************/

    // 특정 RoomMenu의 옵션 목록 조회
    @GetMapping("/list/{roomMenuNum}")
    public ResponseEntity<List<RoomMenuOptionDTO>> getOptionList(@PathVariable Long roomMenuNum) {
        log.info("옵션 조회 컨트롤러 진입");
        List<RoomMenuOptionDTO> options = roomMenuOptionService.roomMenuOptionAllList(roomMenuNum);
        return ResponseEntity.ok(options);
    }

    /**************************************************
     * 메소드명 : createOption
     * 룸 메뉴 옵션 생성
     * 기능: 특정 룸서비스 메뉴(roomMenuNum)에 새로운 옵션(RoomMenuOptionDTO)을 생성하고,
     * 생성된 옵션 정보를 HTTP 상태 코드 201 (Created)과 함께 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-25
     * 수정일 : -
     **************************************************/

    @PostMapping("/create/{roomMenuNum}")
    public ResponseEntity<RoomMenuOptionDTO> createOption(@PathVariable Long roomMenuNum,
                                                          @RequestBody RoomMenuOptionDTO roomMenuOptionDTO) {
        log.info("새로운 옵션 생성 컨트롤러 진입");
        RoomMenuOptionDTO createdOption = roomMenuOptionService.roomMenuOptionInsert(roomMenuNum, roomMenuOptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    /**************************************************
     * 메소드명 : updateOption
     * 룸 메뉴 옵션 수정
     * 기능: 특정 룸서비스 메뉴 옵션(roomMenuOptionNum)의 정보를 전달받은 DTO 내용으로 수정하고,
     * 수정된 옵션 정보를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-25
     * 수정일 : -
     **************************************************/

    // 옵션 수정
    @PutMapping("/update/{roomMenuOptionNum}")
    public ResponseEntity<RoomMenuOptionDTO> updateOption(@PathVariable Long roomMenuOptionNum,
                                                          @RequestBody RoomMenuOptionDTO roomMenuOptionDTO) {
        log.info("옵션 수정 컨트롤러 진입");
        RoomMenuOptionDTO updatedOption = roomMenuOptionService.roomMenuOptionUpdate(roomMenuOptionNum, roomMenuOptionDTO);
        return ResponseEntity.ok(updatedOption);
    }

    /**************************************************
     * 메소드명 : deleteOption
     * 룸 메뉴 옵션 삭제
     * 기능: 특정 룸서비스 메뉴 옵션(roomMenuOptionNum)을 삭제합니다.
     * 성공 시 내용 없음을 나타내는 HTTP 상태 코드 204 (No Content)를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-25
     * 수정일 : -
     **************************************************/

    // 옵션 삭제
    @DeleteMapping("/delete/{roomMenuOptionNum}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long roomMenuOptionNum) {
        log.info("옵션 삭제 컨트롤러 진입");
        roomMenuOptionService.roomMenuOptionDelete(roomMenuOptionNum);
        return ResponseEntity.noContent().build();
    }

}
