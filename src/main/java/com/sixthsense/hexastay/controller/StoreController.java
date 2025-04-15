/***********************************************
 * 클래스명 : StoreController
 * 기능 : 외부업체를 등록 및 기타 기능을 사용하는 페이지와 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store")
public class StoreController {
    private final StoreService storeService;
    private final CompanyService companyService;
    private final AdminService adminService; //adminRepository에는 email로 찾아오는게 있는데.. 여긴 없음.

    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert") // TODO: 호텔 GM만 접근 가능한 페이지
    public String insert(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }

        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        log.info(adminDTO);

        // 소속된 회사 없음
        if (adminDTO.getCompanyNum() == null) {
            if (adminDTO.getStoreNum() != null) {
                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
            } else {
                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
                return "redirect:/admin/logout";
            }
        }

        // 정상적인 호텔 소속 어드민
        CompanyDTO companyDTO = companyService.companyRead(adminDTO.getCompanyNum());
        model.addAttribute("data", companyDTO);
        return "store/insert";
    }

    @PostMapping("/insert")
    public String insert(StoreDTO storeDTO) throws IOException {
//        log.info("등록post : "+storeDTO);
        storeService.insert(storeDTO);
        return "redirect:/admin/store/list";
    }


    @GetMapping("/list")/*todo superAdmin만 접근 가능한 페이지*/
    public String list(Model model, Principal principal){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        log.info(adminDTO);

        // 소속된 회사 없음
        if (adminDTO.getCompanyNum() == null) {
            if (adminDTO.getStoreNum() != null) {
                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
            } else {
                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
                return "redirect:/admin/logout";
            }
        }
        List<StoreDTO> list = storeService.list(adminDTO.getCompanyNum());
        model.addAttribute("list",list);
        return "store/list";
    }


    @GetMapping("/read")
    public String read(Long idid, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO admin = adminService.adminFindEmail(principal.getName());
        if (idid == null) {
            if(admin.getStoreNum()==null){
                log.info("스토어소속이 아닌데 /admin/store/read로 접근. /list로 반환한다.");
                return "redirect:/admin/store/list";
            }else{
                idid = admin.getStoreNum();
            }
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
    public String modify(@PathVariable Long id,Principal principal, Model model){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        StoreDTO data = storeService.read(id);
        storeService.validStoreAdmin(adminDTO,data);
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
