/***********************************************
 * 클래스명 : FacilitiesController
 * 기능 : 외부시설을 관리하는 컨트롤러입니다.
 * 외부시설 등록 수정 같은거는 company에서 담당하고 있으니까 여기는 외부시설서비스 관련...
 * 작성자 : 김예령
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.FacilitiesDTO;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.FsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@Log4j2
@RequiredArgsConstructor
public class FacilitiesController {
    private final FsService fsService;
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

    @GetMapping("/facility/modify/{fsNum}")
    public String fsmodify(@PathVariable Long fsNum, Model model){
        //todo 시설 서비스 수정 (관리자용)
        return "facilities/modify";
    }
    @PostMapping("/facility/modify")
    public String fsmodifyPost(FacilitiesDTO dto){
        //todo 시설 서비스 수정 (관리자용)
        return "redirect:/facility/read/"+dto.getCompanyDTO().getCompanyNum();
    }

    @GetMapping("/facility/delete/{fsNum}")
    public String fsdelete(@PathVariable Long fsNum){
        //todo 시설 서비스 삭제? (관리자용)
        try {
            Long result = fsService.delete(fsNum);
            return "redirect:/facility/list/"+result;
        } catch (Exception e) {
            log.info("삭제할수엄슴");
            return "redirect:/facility/read";
        }
    }

    @GetMapping("/fs/list")
    public String fslistClient(){
        return "facilities/mobile/list";
    }
    @GetMapping("/fs/read")
    public String fsreadClient(){
        return "facilities/mobile/read";
    }
}
