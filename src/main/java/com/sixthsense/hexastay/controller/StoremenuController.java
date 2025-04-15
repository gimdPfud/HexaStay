/***********************************************
 * 클래스명 : StoremenuController
 * 기능 : 외부업체가 제공하는 서비스 메뉴와 관련된 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-02
 * 수정 : 2025-04-02
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.StoreService;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store/menu")
public class StoremenuController {
    private final StoremenuService storemenuService;
    private final StoreService storeService;
    private final AdminService adminService;

    /*storeNum없이 바로 메뉴 등록하려고 할 때
    *   어떤 가게에 메뉴를 추가하시겠습니까? 하는 페이지*/
    @GetMapping("/insert")
    public String insertPrevGet(HttpSession session,Principal principal, Model model){
        if(principal==null){
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if(adminDTO==null){
            return "redirect:/admin/logout";
        }
        if(adminDTO.getStoreNum()!=null){
            return "redirect:/admin/store/menu/insert/"+adminDTO.getStoreNum();
        }
        session.setAttribute("prevpage", 1);        //이전페이지가 /store/menu/insert 이면 1
        List<StoreDTO> list = storeService.getAllList();
        model.addAttribute("list",list);
        return "storemenu/selectstorenum";
    }
    @GetMapping("/list")
    public String listPrevGet(HttpSession session, Principal principal, Model model){
        if(principal==null){
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if(adminDTO==null){
            return "redirect:/admin/logout";
        }
        if(adminDTO.getStoreNum()!=null){
            return "redirect:/admin/store/read?idid="+adminDTO.getStoreNum();
        }
        session.setAttribute("prevpage", 2);        //이전페이지가 /store/menu/list 이면 2
        List<StoreDTO> list = storeService.getAllList();
        model.addAttribute("list",list);
        return "storemenu/selectstorenum";
    }
    @GetMapping("/selected")
    public String insertPrevSelected(HttpSession session, Principal principal, Long storeNum, RedirectAttributes model){
        log.info(storeNum);
        int prevpage = (int) session.getAttribute("prevpage");
        if(prevpage==1){
            return "redirect:/admin/store/menu/insert/"+storeNum;
        } else if(prevpage==2){
            return "redirect:/admin/store/read?idid="+storeNum;
        }
        else {
            log.info("뭔진모르겟는데 오류");
            return null;
        }
    }






    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert/{id}")
    public String insertGet(@PathVariable Long id, Model model){
        log.info("등록 : "+id);
        model.addAttribute("storeNum",id);
        return "storemenu/insert";
    }
    @PostMapping("/insert")
    public String insertPost(StoremenuDTO storemenuDTO) throws IOException {
        storemenuService.insert(storemenuDTO);
        return "redirect:/admin/store/read?idid="+storemenuDTO.getStoreNum();
    }


    @ResponseBody
    @GetMapping("/list/{id}")
    public ResponseEntity listGet(@PathVariable Long id){
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id);
        if(menulist.isEmpty()){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }

    @ResponseBody
    @GetMapping("/list/deleted/{id}")
    public ResponseEntity deletedlistGet(@PathVariable Long id){
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id,"deleted");
        if(menulist.isEmpty()){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }
    @ResponseBody
    @GetMapping("/list/soldout/{id}")
    public ResponseEntity soldoutlistGet(@PathVariable Long id){
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuDTO> menulist = storemenuService.list(id,"soldout");
        if(menulist.isEmpty()){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
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
    public String modify(StoremenuDTO storemenuDTO) throws IOException {
        Long storemenuNum = storemenuService.modify(storemenuDTO);
        log.info(storemenuNum);
        return "redirect:/admin/store/menu/read/"+storemenuNum;
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
