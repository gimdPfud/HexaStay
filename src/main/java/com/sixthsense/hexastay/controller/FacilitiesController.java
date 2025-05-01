/***********************************************
 * 클래스명 : FacilitiesController
 * 기능 : 외부시설을 관리하는 컨트롤러입니다.
 * 외부시설 등록 수정 같은거는 company에서 담당하고 있으니까 여기는 외부시설서비스 관련...
 * 작성자 : 김예령
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.FacilitiesDTO;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.FsService;
import com.sixthsense.hexastay.service.ZzService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;

@Controller
@Log4j2
@RequiredArgsConstructor
public class FacilitiesController {
    private final FsService fsService;
    private final ZzService zzService;
    private final CompanyService companyService;
    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/facility/insert/{companyNum}")
    public String fsinsert(@PathVariable Long companyNum, Model model){
        try {
            model.addAttribute("company", companyService.companyRead(companyNum).getCompanyName());
            model.addAttribute("companyNum", companyService.companyRead(companyNum).getCompanyNum());
            return "facilities/insert";
        } catch (Exception e){
            log.info("예외발생");
            return "redirect:/company/read/"+companyNum;
        }
    }
    @PostMapping("/facility/insert")
    public String fsinsertPost(FacilitiesDTO dto, @RequestParam Long companyNum) throws IOException {
        try {
            dto.setCompanyDTO(companyService.companyRead(companyNum));
            Long result = fsService.fsInsert(dto);
            return "redirect:/facility/list/"+result;
        } catch (Exception e){
            log.info("예외발생");
            return "redirect:/facility/insert/"+companyNum;
        }
    }

    @GetMapping("/facility/list/{companyNum}")
    public String fslist(@PathVariable Long companyNum, Model model){
        model.addAttribute("list",fsService.list(companyNum));
        return "facilities/list";
    }

    @GetMapping("/facility/read/{fsNum}")
    public String fsread(@PathVariable Long fsNum, Model model){
        model.addAttribute("data",fsService.read(fsNum));
        return "facilities/read";
    }

    @PostMapping("/facility/activate/{num}")
    public String fsYesPost(@PathVariable Long num, Model model){
        try {
            num = fsService.fsYes(num);
        } catch (Exception e) {
            model.addAttribute("errmsg","상태를 변경할 수 없습니다.");
        }
        return "redirect:/facility/read/"+num;
    }
    @PostMapping("/facility/deactivate/{num}")
    public String fsNoPost(@PathVariable Long num, Model model){
        try {
            num = fsService.fsNo(num);
        } catch (Exception e) {
            model.addAttribute("errmsg","상태를 변경할 수 없습니다.");
        }
        return "redirect:/facility/read/"+num;
    }
    @PostMapping("/facility/refill/{num}")
    public String fsRefillPost(@PathVariable Long num, Model model){
        try {
            num = fsService.refill(num);
        } catch (Exception e) {
            model.addAttribute("errmsg","수량을 변경할 수 없습니다.");
        }
        return "redirect:/facility/list/"+num;
    }

    @GetMapping("/facility/modify/{fsNum}")
    public String fsmodify(@PathVariable Long fsNum, Model model){
        model.addAttribute("data",fsService.read(fsNum));
        return "facilities/modify";
    }
    @PostMapping("/facility/modify")
    public String fsmodifyPost(FacilitiesDTO dto, Model model){
        log.info(dto.toString());
        try {
            Long num = fsService.modify(dto);
            return "redirect:/facility/list/"+num;
        }catch (Exception e){
            log.info("수정불가능");
            model.addAttribute("errmsg","상태를 변경할 수 없습니다.");
            return "redirect:/facility/modify/"+dto.getFacilitiesNum();
        }
    }

    @GetMapping("/facility/delete/{fsNum}")
    public String fsdelete(@PathVariable Long fsNum, HttpServletRequest request){
        try {
            Long result = fsService.delete(fsNum);
            return "redirect:/facility/list/"+result;
        } catch (Exception e) {
            log.info("삭제할수엄슴");
            String referer = request.getHeader("Referer");
            return "redirect:"+referer;
        }
    }

    @GetMapping("/fs/list")
    public String fslistClient(Principal principal, Pageable pageable, Model model){
//        log.info("현재 로그인한 사용자: " + (principal != null ? principal.getName() : "없음"));
        if(principal==null){
            return "redirect:/cart/qr";}
        Long companyNum = 0L;
        try {
            companyNum = zzService.hotelroomNumToCompany(zzService.principalToHotelroomNum(principal)).getCompanyNum();
            log.info("컴퍼니넘 : "+companyNum);
        } catch (Exception e){
            log.info("오류발생");
            return "redirect:/main";
        }
        Page< CompanyDTO> list = companyService.companySearchList(null,"facility",null,companyNum,pageable);
        model.addAttribute("list",list);
        return "facilities/mobile/list";
    }
    @GetMapping("/fs/read/{num}")
    public String fsreadClient(@PathVariable Long num, Model model){
//        model.addAttribute("data",)
        return "facilities/mobile/read";
    }
}
