/***********************************************
 * 클래스명 : StoremenuController
 * 기능 : 외부업체가 제공하는 서비스 메뉴와 관련된 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-02
 * 수정 : 2025-04-02
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.service.StoremenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/store/menu")
public class StoremenuController {
    private final StoremenuService storemenuService;

    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert/{id}")
    public String insert(@PathVariable Long id, Model model){
        log.info("등록 : "+id);
        model.addAttribute("storeNum",id);
        return "storemenu/insert";
    }
    @PostMapping("/insert")
    public String insert(StoremenuDTO storemenuDTO){
        storemenuService.insert(storemenuDTO);
        return "redirect:/store/menu/insert/"+storemenuDTO.getStoreNum();
    }

/*todo 이거 /store/read/{id}에 어떻게 넣는지 생각좀 해보기...*/
    @ResponseBody
    @GetMapping("/list/{id}")
    public ResponseEntity list(@PathVariable Long id, Pageable pageable){
        /*storeNum으로 Menu 가져오기...*/
        Page<StoremenuDTO> menulist = storemenuService.list(id, "active", pageable);
        menulist.forEach(log::info);
        return new ResponseEntity<>(menulist, HttpStatus.OK);
    }


    @GetMapping("/read/{id}")
    public String read(@PathVariable Long id, Model model){
        log.info("메뉴상세보기 메뉴Num: "+id);
        StoremenuDTO data = storemenuService.read(id);
        model.addAttribute("data",data);
        return "storemenu/read";
    }


    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model){
        StoremenuDTO data = storemenuService.read(id);
        model.addAttribute("data",data);
        return "storemenu/modify";
    }
    @PostMapping("/modify")
    public String modify(StoremenuDTO storemenuDTO){
        storemenuService.modify(storemenuDTO);
        return "redirect:/store/menu/read/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        return "redirect:/store/menu/list";
    }
}
