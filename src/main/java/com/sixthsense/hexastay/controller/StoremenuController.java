/***********************************************
 * 클래스명 : StoremenuController
 * 기능 : 외부업체가 제공하는 서비스 메뉴와 관련된 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-02
 * 수정 : 2025-04-02
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store/menu")
public class StoremenuController {
    private final StoremenuService storemenuService;
//    private final StoreService storeService;
//    private final AdminService adminService;
//    /*storeNum없이 바로 메뉴 등록하려고 할 때
//    *   어떤 가게에 메뉴를 추가하시겠습니까? 하는 페이지*/
//    @GetMapping("/insert")
//    public String insertPrevGet(HttpSession session,Principal principal, Model model){
//        if(principal==null){
//            return "redirect:/admin/login";
//        }
//        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
//        if(adminDTO==null){
//            return "redirect:/admin/logout";
//        }
//        if(adminDTO.getStoreNum()!=null){
//            return "redirect:/admin/store/menu/insert/"+adminDTO.getStoreNum();
//        }
//        session.setAttribute("prevpage", 1);        //이전페이지가 /store/menu/insert 이면 1
//        List<StoreDTO> list = storeService.getAllList();
//        model.addAttribute("valid","서비스 등록");
//        model.addAttribute("list",list);
//        return "storemenu/selectstorenum";
//    }
//    @GetMapping("/list")
//    public String listPrevGet(HttpSession session, Principal principal, Model model){
//        if(principal==null){
//            return "redirect:/admin/login";
//        }
//        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
//        if(adminDTO==null){
//            return "redirect:/admin/logout";
//        }
//        if(adminDTO.getStoreNum()!=null){
//            return "redirect:/admin/store/read?idid="+adminDTO.getStoreNum();
//        }
//        session.setAttribute("prevpage", 2);        //이전페이지가 /store/menu/list 이면 2
//        List<StoreDTO> list = storeService.getAllList();
//        model.addAttribute("valid","서비스 목록");
//        model.addAttribute("list",list);
//        return "storemenu/selectstorenum";
//    }
//    @GetMapping("/selected")
//    public String insertPrevSelected(HttpSession session, Principal principal, Long storeNum, Model model){
//        log.info(storeNum);
//        int prevpage = (int) session.getAttribute("prevpage");
//        if(prevpage==1){
//            return "redirect:/admin/store/menu/insert/"+storeNum;
//        } else if(prevpage==2){
//            return "redirect:/admin/store/read?idid="+storeNum;
//        }
//        else {
//            log.info("뭔진모르겟는데 오류");
//            return null;
//        }
//    }
    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert/{id}")
    public String insertGet(@PathVariable Long id, Model model){
//        log.info("등록 : "+id);
        model.addAttribute("storeNum",id);
        return "storemenu/insert";
    }
    @PostMapping("/insert")
    public String insertPost(@Valid StoremenuDTO storemenuDTO, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()){
            log.info("유효성오류발생");
            bindingResult.getAllErrors().forEach(log::info);
            return "redirect:/admin/store/menu/insert/"+storemenuDTO.getStoreNum();
        }
        storemenuService.insert(storemenuDTO);
        return "redirect:/admin/store/read?idid="+storemenuDTO.getStoreNum();
    }


    @ResponseBody
    @GetMapping("/list/{id}")
    public ResponseEntity<?> listGet(@PathVariable Long id, Locale locale) { // Locale 파라미터 추가
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id, "alive", locale); // locale 전달
        if (menulist == null || menulist.isEmpty()) { // null 체크 추가
            return new ResponseEntity<>("메뉴 목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }

    @ResponseBody
    @GetMapping("/list/deleted/{id}")
    public ResponseEntity<?> deletedlistGet(@PathVariable Long id, Locale locale) { // Locale 파라미터 추가
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id, "deleted", locale); // locale 전달
        if (menulist == null || menulist.isEmpty()) { // null 체크 추가
            return new ResponseEntity<>("삭제된 메뉴 목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }
    @ResponseBody
    @GetMapping("/list/soldout/{id}")
    public ResponseEntity<?> soldoutlistGet(@PathVariable Long id, Locale locale) { // Locale 파라미터 추가
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id, "soldout", locale); // locale 전달
        if (menulist == null || menulist.isEmpty()) { // null 체크 추가
            return new ResponseEntity<>("품절된 메뉴 목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }


    @GetMapping("/read/{id}")
    public String read(@PathVariable Long id, Model model, Locale locale){
//        log.info("메뉴상세보기 메뉴Num: "+id);
        StoremenuDTO data = storemenuService.read(id, locale);
        model.addAttribute("data",data);
        return "storemenu/read";
    }


    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model, Locale locale){
        StoremenuDTO data = storemenuService.read(id, locale);
        model.addAttribute("data",data);
        return "storemenu/modify";
    }
    @PostMapping("/modify")
    public String modify(@Valid StoremenuDTO storemenuDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.info("유효성오류발생");
            bindingResult.getAllErrors().forEach(log::info);
            return "redirect:/admin/store/menu/modify/"+storemenuDTO.getStoremenuNum();
        }
        try {
            Long storemenuNum = storemenuService.modify(storemenuDTO);
            log.info(storemenuNum);
            return "redirect:/admin/store/menu/read/" + storemenuNum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        Long storeNum = storemenuService.delete(id);
        return "redirect:/admin/store/read?idid="+storeNum;
    }
    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id){
        Long storeNum = storemenuService.restore(id);
        return "redirect:/admin/store/read?idid="+storeNum;
    }
    @GetMapping("/soldout/{id}")
    public String soldout(@PathVariable Long id){
        Long storeNum = storemenuService.soldout(id);
        return "redirect:/admin/store/read?idid="+storeNum;
    }
}
