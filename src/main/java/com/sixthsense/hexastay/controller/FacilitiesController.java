/***********************************************
 * 클래스명 : FacilitiesController
 * 기능 : 외부시설을 관리하는 컨트롤러입니다.
 * 외부시설 등록 수정 같은거는 company에서 담당하고 있으니까 여기는 외부시설서비스 관련...
 * 작성자 : 김예령
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
public class FacilitiesController {
    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/admin/facilities/list")
    public void fslist(){
        //todo 시설 서비스 목록 (관리자용)
    }
}
