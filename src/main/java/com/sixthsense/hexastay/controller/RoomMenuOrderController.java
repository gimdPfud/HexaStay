package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-09
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuOrderController {

    private final RoomMenuService roomMenuService;

//    @GetMapping("/roommenu/orderpage/read)
//    @ResponseBody
//    public RoomMenuDTO getRoomMenu(@RequestParam Long roomMenuNum) {
//        log.info("AJAX용 룸메뉴 상세 JSON 요청: {}", roomMenuNum);
//        return roomMenuService.read(roomMenuNum);
//    }
}
