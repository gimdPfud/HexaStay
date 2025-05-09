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
import com.sixthsense.hexastay.dto.StoremenuOptionDTO;
import com.sixthsense.hexastay.service.StoreService;
import com.sixthsense.hexastay.service.StoremenuOptionService;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store/menu/option")
public class StoremenuOptionController {
//    private final StoremenuService storemenuService;
//    private final StoreService storeService;
    private final StoremenuOptionService storemenuOptionService;

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
    public String insertPost(StoremenuOptionDTO storemenuOptionDTO) throws IOException {
        storemenuOptionService.insert(storemenuOptionDTO);
        return "redirect:/admin/store/menu/read/"+storemenuOptionDTO.getStoremenuNum();
    }


    @ResponseBody
    @GetMapping("/list/{id}")
    public ResponseEntity listGet(@PathVariable Long id){
        /*storeNum으로 Menu 가져오기...*/
        List<StoremenuOptionDTO> menulist = storemenuOptionService.list(id);
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
        List<StoremenuOptionDTO> menulist = storemenuOptionService.list(id,"deleted");
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
        List<StoremenuOptionDTO> menulist = storemenuOptionService.list(id,"soldout");
        if(menulist.isEmpty()){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(menulist, HttpStatus.OK);
        }
    }

    @ResponseBody
    @GetMapping("/modify/{id}")
    public ResponseEntity modify(@PathVariable Long id){
        StoremenuOptionDTO data = storemenuOptionService.read(id);
        if(data==null){
            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }
    @PostMapping("/modify")
    public String modify(StoremenuOptionDTO storemenuOptionDTO) throws IOException {
        Long storemenuNum = storemenuOptionService.modify(storemenuOptionDTO);
        log.info(storemenuNum);
        return "redirect:/admin/store/menu/read/"+storemenuNum;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        Long storemenuNum = storemenuOptionService.delete(id);
        return "redirect:/admin/store/menu/read/"+storemenuNum;
    }
    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id){
        Long storemenuNum = storemenuOptionService.restore(id);
        return "redirect:/admin/store/menu/read/"+storemenuNum;
    }

    //------------ Rest 방식 ------------//
    @ResponseBody
    @GetMapping("/del/{id}")
    public ResponseEntity deleteRest(@PathVariable Long id){
        try {
            storemenuOptionService.delete(id);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping("/res/{id}")
    public ResponseEntity restoreRest(@PathVariable Long id){
        try {
            storemenuOptionService.restore(id);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
