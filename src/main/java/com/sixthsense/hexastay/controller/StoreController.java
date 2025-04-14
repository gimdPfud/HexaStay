/***********************************************
 * 클래스명 : StoreController
 * 기능 : 외부업체를 등록 및 기타 기능을 사용하는 페이지와 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store")
public class StoreController {
    private final StoreService storeService;
    private final AdminService adminService; //adminRepository에는 email로 찾아오는게 있는데.. 여긴 없음.

    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert") //todo hotel sv 접근 가능한 페이지
    public String insert(Principal principal, Model model){
//        log.info("등록");
        if(principal==null){
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        model.addAttribute("companyNum",adminDTO.getCompanyNum());
        return "store/insert";
    }
    @PostMapping("/insert")
    public String insert(StoreDTO storeDTO) throws IOException {
//        log.info("등록post : "+storeDTO);
        storeService.insert(storeDTO);
        return "redirect:/admin/store/list";
    }


    @GetMapping("/list")/*todo superAdmin만 접근 가능한 페이지*/
    public String list(Pageable pageable, Model model){
        Page<StoreDTO> storeDTOPage = storeService.list("alive", pageable);
//        storeDTOPage.forEach(log::info);
        model.addAttribute("list",storeDTOPage);
        return "store/list";
    }


    @GetMapping("/read")
    public String read(Long idid, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO admin = adminService.adminFindEmail(principal.getName());
        if (idid == null) {
            idid = admin.getStoreNum();
        }
        StoreDTO data = storeService.read(idid);
        boolean result = storeService.validStoreAdmin(admin, data);
        if (result) {
            model.addAttribute("data", data);
            return "store/read";
        } else {
            log.info("다른 가게의 상세정보를 보려고 함.");
            return "redirect:/admin/logout";
        }
    }


    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model){
        StoreDTO data = storeService.read(id);
        model.addAttribute("data",data);
        return "store/modify";
    }
    @PostMapping("/modify")
    public String modify(StoreDTO storeDTO) throws IOException {
        Long storeNum = storeService.modify(storeDTO);
        return "redirect:/admin/store/read?idid="+storeNum;
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        storeService.delete(id);
        return "redirect:/admin/store/list";
    }
}
