/***********************************************
 * 클래스명 : StoreClientController
 * 기능 : 고객전용 페이지 (1. 스토어 목록보기,
 * 2. 스토어 상세 및 3. 메뉴 목록, 4. 메뉴 상세, 5. 장바구니, 6. 외부메뉴결제까지.)
 * 작성자 : 김예령
 * 작성일 : 2025-04-07
 * 수정 : 2025-04-07
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.service.StoreService;
import com.sixthsense.hexastay.service.StoremenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order/store")
public class StoreClientController {
    private final StoreService storeService;
    private final StoremenuService storemenuService;

/* 1. 스토어 목록 보기
        get. */
    @GetMapping("/list")
    public String list(Model model, Pageable pageable){
        Page<StoreDTO> storeDTOPage = storeService.clientlist(pageable);
        model.addAttribute("list",storeDTOPage);
        return "mobilestore/list";
    }

/* 2. 스토어 상세 보기
        get. */
    @GetMapping("/read/{storeNum}")
    public String storeRead(@PathVariable Long storeNum, Model model){
        StoreDTO storeDTO = storeService.read(storeNum);
        model.addAttribute("data", storeDTO);
        return "mobilestore/read";
    }

/* 3. 스토어메뉴 목록 보기 (rest)
        get. */
    @ResponseBody
    @GetMapping("/menu/list/{storeNum}")
    public ResponseEntity menulist(@PathVariable Long storeNum){
        List<StoremenuDTO> storemenuDTOList = storemenuService.list(storeNum);
        if(storemenuDTOList.isEmpty()){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(storemenuDTOList, HttpStatus.OK);
        }
    }

/* 4. 스토어메뉴 상세 보기
        get. */

/* 5. 장바구니페이지 이동해서 보기.
           ??......get?*/

/* 6. 결제하기
        get? post? */
}