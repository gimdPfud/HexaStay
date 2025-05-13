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
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

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
    @GetMapping("/insert")
    public String insert(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
//        log.info(adminDTO);
//        // 소속된 회사 없음
//        if (adminDTO.getCompanyNum() == null) {
//            if (adminDTO.getStoreNum() != null) {
//                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
//                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
//            } else {
//                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
//                return "redirect:/admin/logout";
//            }
//        }
        model.addAttribute("companyList",companyService.getBnFList());
        return "store/insert";
    }

    @PostMapping("/insert")
    public String insert(@Valid StoreDTO storeDTO, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()){
            log.info("유효성오류발생");
            bindingResult.getAllErrors().forEach(log::info);
            return "redirect:/admin/store/insert";
        }
        storeService.insert(storeDTO);
        return "redirect:/admin/store/list";
    }


    /* post 없애고 get으로만???
    *   맨 처음 get에서도 로그인한 사람의 companyNum으로 store목록을 보여줘야 함
    *   그래서 굳이 get post 둘 다 하지 말고.. 그냥 get한번에만 하는걸로... post는 하지말어?*/
    @GetMapping("/list")
    public String list(Pageable pageable, Model model, Principal principal,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) List<Long> companyNum,
                       @RequestParam(required = false) String keyword){
        log.info("겟겟겟리스트");
        if(companyNum!=null){
            log.info(companyNum.toString());
        }

        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        if (adminDTO.getStoreNum()!=null){
            return "redirect:/admin/store/read?idid="+adminDTO.getStoreNum();
        }

        // 프린시펄로 companyNum
        // admin마다 볼 수 있는 업체가 달라야함
        // 사장님 : 내가게만
        // 지사 : 본인 소속 업체만
        // 본사 : 본인 소속 업체 + 본인 소속 지사 업체
        // 슈퍼어드민 : 전부다?
        //필요한거 : 어드민이 볼 수 있는 companyNum 배열
        //입력 : adminDTo 반환 : List<Long> companyNum
        if(companyNum==null || companyNum.isEmpty() || companyNum.contains(0L)){
            companyNum = storeService.getCompanyNums(adminDTO);
        }

        Page<StoreDTO> list = storeService.searchlist(companyNum, searchType, keyword, pageable, "alive", "closed");
        model.addAttribute("list",list);
        Page<StoreDTO> listA = storeService.searchlist(companyNum, searchType, keyword, pageable, "deleted");
        model.addAttribute("deletedList",listA);
//        model.addAttribute("companyList",companyService.getBnFList());
        model.addAttribute("companyMap", storeService.getCompanyMap(adminDTO));
        model.addAttribute("searchType",searchType);
        model.addAttribute("chosenCompany",companyNum);
        model.addAttribute("keyword",keyword);
        return "store/list";
    }
//    @PostMapping("/list")
//    public String list(Model model, Principal principal, Pageable pageable,
//                       @RequestParam(required = false) String searchType,
//                       @RequestParam(required = false) String chosenCompany,
//                       @RequestParam(required = false) String keyword){
//        log.info("searchType : " + searchType);
//        log.info("chosenCompany : " + chosenCompany);
//        log.info("keyword : " + keyword);
//        log.info("pageable : " + pageable.toString());
//        if (principal == null) {
//            return "redirect:/admin/login";
//        }
//        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
//        if (adminDTO == null) {
//            return "redirect:/admin/logout";
//        }
// 소속된 회사 없음
//        if (adminDTO.getCompanyNum() == null) {
//            if (adminDTO.getStoreNum() != null) {
//                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
//                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
//            } else {
//                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
//                return "redirect:/admin/logout";
//            }
//        }
//        Long companyNum = 0L;
//        try {companyNum = Long.valueOf(chosenCompany);}
//        catch (NumberFormatException ignored){}
//        model.addAttribute("companyNum",companyNum);
//        log.info(companyNum);
//        Page<StoreDTO> list = storeService.searchlist(companyNum, searchType, keyword, pageable);
//        list.forEach(log::info);
//        Page<StoreDTO> list = storeService.searchlist(companyNum, searchType, keyword, pageable,"alive","closed");
//        Page<StoreDTO> listA = storeService.searchlist(companyNum, searchType, keyword, pageable,"deleted");
//        model.addAttribute("list",list);
//        model.addAttribute("deletedList",listA);
//        model.addAttribute("companyMap", storeService.getCompanyMap());
//        model.addAttribute("searchType",searchType);
//        model.addAttribute("keyword",keyword);
//        return "store/list";
//    }


    @GetMapping("/read")
    public String read(Long idid, Principal principal, Model model, Locale locale) {
        log.info("Admin store read request for idid: {}, locale: {}", idid, locale);

        if (principal == null) {
            return "redirect:/admin/login";
        }
        if(idid==null){
            return null;
        }
        AdminDTO admin = adminService.adminFindEmail(principal.getName());
        StoreDTO data = storeService.read(idid, locale);
        boolean result = storeService.validStoreAdmin(admin, data);
        if (result) {
            model.addAttribute("data", data);
            model.addAttribute("currentLang", locale.getLanguage());
            return "store/read";
        } else {
            log.info("Admin {} attempted to access unauthorized store {}", admin.getAdminEmail(), idid); // 로그 개선
            return "redirect:/admin/logout"; // 또는 접근 거부 페이지
        }
    }


    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Principal principal, Model model, Locale locale){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        StoreDTO data = storeService.read(id, locale);
        if (!storeService.validStoreAdmin(adminDTO, data)) {
            return "redirect:/admin/logout"; // 또는 접근 거부 페이지
        }
        model.addAttribute("data", data);
        // TODO: 카테고리 리스트 다국어 처리 필요 시 MessageSource 사용 고려하는것도 괜찮은 방법일지도..?
        model.addAttribute("storeCategoryList", List.of("한식", "중식", "일식", "아시안", "양식", "패스트푸드", "카페"));
        model.addAttribute("currentLang", locale.getLanguage());
        return "store/modify";
    }

    @PostMapping("/modify")
    public String modify(@Valid StoreDTO storeDTO, BindingResult bindingResult, RedirectAttributes model){
        log.info(storeDTO.toString());
        if(bindingResult.hasErrors()){
            log.info("유효성체크");
            bindingResult.getAllErrors().forEach(log::info);
            model.addAttribute("errmsg","수정에 실패했습니다. 내용을 다시 확인해주세요.");
            return "redirect:/admin/store/modify/"+storeDTO.getStoreNum();
        }
        try {
            Long storeNum = storeService.modify(storeDTO);
            return "redirect:/admin/store/read?idid="+storeNum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        storeService.delete(id);
        return "redirect:/admin/store/list";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id){
        storeService.restore(id);
        return "redirect:/admin/store/list";
    }
}
