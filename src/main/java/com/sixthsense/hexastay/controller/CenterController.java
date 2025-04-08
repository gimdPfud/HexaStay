package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.service.BranchService;
import com.sixthsense.hexastay.service.CenterService;
import com.sixthsense.hexastay.service.FacilityService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.codehaus.groovy.transform.sc.transformers.RangeExpressionTransformer;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@ToString
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;
    private final BranchService branchService;
    private final FacilityService facilityService;

    @GetMapping("/list")
    public String listCenter(Model model, Pageable pageable){
        log.info("get 방식 center 목록 controller 진입");

        //페이지네이션된 본사 목록 가져와서 Page<CenterDTO> 형태로 반환
        Page<CenterDTO> centerDTOS = centerService.centerList(pageable);
        //model을 통해 view에 전달
        model.addAttribute("centerDTOS", centerDTOS);

        return "center/list";
    }

    //검색용 - 검색 조건과 검색어를 받아서 처리
    @GetMapping("/listsearch")
    public String listSearch(@RequestParam String select,   //검색 조건
                             @RequestParam String choice,   //선택한 항목 종류(소속)(center, branch, facility)
                             @RequestParam String keyword,  //검색어
                             Pageable pageable,
                             Model model) {

        if (!keyword.isEmpty()) {
            // 키워드가 있을 때 - 검색 조건
            if (select.equals("company")) {     //검색 조건이 조직명이며
                if (choice.equals("center")) {  //소속 설정이 본사일 때, model을 통해 목록을 불러와서 보여줌
                    //본사 중 상호명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("centerDTOS", centerService.companyName(keyword, pageable));
                } else if (choice.equals("branch")) {   //소속 설정이 지사일 때
                    //지사 중 상호명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("branchDTOS", branchService.companyName(keyword, pageable));
                } else if (choice.equals("facility")) { //소속 설정이 지점일 때
                    //지점 중 상호명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("facilityDTOS", facilityService.companyName(keyword, pageable));
                }

            } else if (select.equals("brandName")) {    //검색 조건이 브랜드명이며
                if (choice.equals("center")) {          //소속 설정이 본사일 때, model을 통해 목록을 불러와서 보여줌
                    //본사 중 브랜드명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("centerDTOS", centerService.brandName(keyword, pageable));
                } else if (choice.equals("branch")) {   //소속 설정이 지사일 때
                    //지사 중 브랜드명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("branchDTOS", branchService.brandName(keyword, pageable));
                } else if (choice.equals("facility")) { //소속 설정이 지점일 때
                    //지점 중 브랜드명이 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("facilityDTOS", facilityService.brandName(keyword, pageable));
                }

            } else if (select.equals("businessNum")) {  //검색 조건이 사업자등록번호이며
                if (choice.equals("center")) {          //소속 설정이 본사일 때, model을 통해 목록을 불러와서 보여줌
                    //본사 중 사업자등록번호가 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("centerDTOS", centerService.centerBusinessNum(keyword, pageable));
                } else if (choice.equals("branch")) {   //소속 설정이 지사일 때
                    //지사 중 사업자등록번호가 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("branchDTOS", branchService.branchBusinessNum(keyword, pageable));
                } else if (choice.equals("facility")) { //소속 설정이 지점일 때
                    //지점 중 사업자등록번호가 검색어를 포함하는(containing) 목록을 검색
                    model.addAttribute("facilityDTOS", facilityService.facilityBusinessNum(keyword, pageable));
                }
            }

        } else {
            // 키워드가 비어있으며
            if (choice.equals("center")) {  //소속 설정이 본사일 때, model을 통해 본사 전체 목록을 불러와서 보여줌
                model.addAttribute("centerDTOS", centerService.centerList(pageable));
            } else if (choice.equals("branch")) {   //소속 설정이 지사일 때
                model.addAttribute("branchDTOS", branchService.branchList(pageable));
            } else if (choice.equals("facility")) { //소속 설정이 지점일 때
                model.addAttribute("facilityDTOS", facilityService.facilityList(pageable));
            }
        }

        if (select.equals("전체")) {  //검색 조건이 전체이며
            if (choice.equals("center")) {  //소속 설정이 본사일 때, model을 통해 본사 전체 목록을 불러와서 보여줌
                model.addAttribute("centerDTOS", centerService.centerList(pageable));
            } else if (choice.equals("branch")) {   //소속 설정이 지사일 때
                model.addAttribute("branchDTOS", branchService.branchList(pageable));
            } else if (choice.equals("facility")) { //소속 설정이 지점일 때
                model.addAttribute("facilityDTOS", facilityService.facilityList(pageable));
            }
        }

        model.addAttribute("select", select);
        model.addAttribute("choice", choice);
        model.addAttribute("keyword", keyword);

        return "center/list";
    }

    @GetMapping("/signup")
    public String signUpCenterGet(Model model){
        log.info("get 방식 center 등록 controller 진입");

        //get 등록 (본사 전체 목록을 불러옴(검색하기 전에) DTO에 담아 model을 통해 보여준다.)
        List<CenterDTO> centerDTOList = centerService.allCenterList();
        model.addAttribute("centerDTOList", centerDTOList);

        return "center/signup";
    }

    @PostMapping("/signup")
    public String signUpCenterPost(CenterDTO centerDTO){
        log.info("post 방식 center 등록 controller 진입");

        centerService.centerInsert(centerDTO);

        return "redirect:/center/list";
    }

    @GetMapping("/read/{centerNum}")
    public String readCenter(@PathVariable("centerNum") Long centerNum, Model model, RedirectAttributes redirectAttributes) {
        log.info("get 방식 center 상세보기 controller 진입");

        CenterDTO centerDTO = centerService.centerRead(centerNum);

        if(centerDTO == null){
            redirectAttributes.addFlashAttribute("msg", "해당 본사 정보를 찾을 수 없습니다.");
            return "redirect:/center/list";
        }

        model.addAttribute("centerDTO", centerDTO);

        return "center/read";
    }

    @GetMapping("/modify/{centerNum}")
    public String modifyCenterGet(@PathVariable("centerNum") Long centerNum, Model model) {
        log.info("get 방식 center 수정 controller 진입");

        if(centerNum == null){
            log.info("centerNum을 찾을 수 없음");
            return "redirect:/center/list";
        }

        CenterDTO centerDTO = centerService.centerRead(centerNum);

        model.addAttribute("centerDTO", centerDTO);

        return "center/modify";
    }

    @PostMapping("/modify/{centerNum}")
    public String modifyCenterPost(@ModelAttribute CenterDTO centerDTO) {
        log.info("post 방식 center 수정 controller 진입");
        centerService.centerModify(centerDTO);

        return "redirect:/center/list";
    }

    @PostMapping("/delete/{centerNum}")
    public String deleteCenter(@PathVariable Long centerNum) {
        log.info("post 방식 center 삭제 controller 진입");

        centerService.centerDelete(centerNum);

        return "redirect:/center/list";
    }

    // 조직등록 - 센터
    @PostMapping("/centerinsert")
    @ResponseBody
    public String centerInsertPost(CenterDTO centerDTO) {
        centerService.centerInsert(centerDTO);
        return "redirect:/center/list";
    }

    // 조직 리스트 조회용
    @PostMapping("/searchcenter")
    @ResponseBody
    public Page<CenterDTO> searchCenterPost(Model model, Pageable pageable) {
        Page<CenterDTO> centerDTOS = centerService.centerList(pageable);
        return centerDTOS;
    }

    // 조직 리스트 조회용 2
    @PostMapping("/searchbranch")
    @ResponseBody
    public Page<BranchDTO> searchBranchPost(Model model, Pageable pageable) {
        Page<BranchDTO> branchDTOS = branchService.branchList(pageable);
        return branchDTOS;
    }

    // 조직 리스트 조회용 3
    @PostMapping("/searchfacility")
    @ResponseBody
    public Page<FacilityDTO> searchFacilityPost(Model model, Pageable pageable) {
        Page<FacilityDTO> facilityDTOS = facilityService.facilityList(pageable);
        return facilityDTOS;
    }


}
