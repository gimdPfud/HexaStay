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

        Page<CenterDTO> centerDTOS = centerService.centerList(pageable);
        model.addAttribute("centerDTOS", centerDTOS);


        return "center/list";
    }

    //검색용
    @GetMapping("/listsearch")
    public String listSearch(@RequestParam String select,
                             @RequestParam String choice,
                             @RequestParam String keyword,
                             Pageable pageable,
                             Model model) {

        if (!keyword.isEmpty()) {
            // 🔍 키워드가 있을 때 - 검색 조건 분기
            if (select.equals("company")) {
                if (choice.equals("center")) {
                    model.addAttribute("centerDTOS", centerService.companyName(keyword, pageable));
                } else if (choice.equals("branch")) {
                    model.addAttribute("branchDTOS", branchService.companyName(keyword, pageable));
                } else if (choice.equals("facility")) {
                    model.addAttribute("facilityDTOS", facilityService.companyName(keyword, pageable));
                }

            } else if (select.equals("brandName")) {
                if (choice.equals("center")) {
                    model.addAttribute("centerDTOS", centerService.brandName(keyword, pageable));
                } else if (choice.equals("branch")) {
                    model.addAttribute("branchDTOS", branchService.brandName(keyword, pageable));
                } else if (choice.equals("facility")) {
                    model.addAttribute("facilityDTOS", facilityService.brandName(keyword, pageable));
                }

            } else if (select.equals("businessNum")) {
                if (choice.equals("center")) {
                    model.addAttribute("centerDTOS", centerService.companyName(keyword, pageable));
                } else if (choice.equals("branch")) {
                    model.addAttribute("branchDTOS", branchService.companyName(keyword, pageable));
                } else if (choice.equals("facility")) {
                    model.addAttribute("facilityDTOS", facilityService.companyName(keyword, pageable));
                }
            }

        } else {
            // 🔄 키워드가 비어 있을 때 - 전체 리스트
            if (choice.equals("center")) {
                model.addAttribute("centerDTOS", centerService.centerList(pageable));
            } else if (choice.equals("branch")) {
                model.addAttribute("branchDTOS", branchService.branchList(pageable));
            } else if (choice.equals("facility")) {
                model.addAttribute("facilityDTOS", facilityService.facilityList(pageable));
            }
        }

        if (select.equals("전체")) {
            if (choice.equals("center")) {
                model.addAttribute("centerDTOS", centerService.centerList(pageable));
            } else if (choice.equals("branch")) {
                model.addAttribute("branchDTOS", branchService.branchList(pageable));
            } else if (choice.equals("facility")) {
                model.addAttribute("facilityDTOS", facilityService.facilityList(pageable));
            }
        }

        return "center/list";
    }


    @GetMapping("/signup")
    public String signUpCenterGet(Model model){
        log.info("get 방식 center 등록 controller 진입");
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
    public String readCenter(@PathVariable("centerNum") Long centerNum, Model model) {
        log.info("get 방식 center 상세보기 controller 진입");

        CenterDTO centerDTO = centerService.centerRead(centerNum);
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

    @PostMapping("/modify")
    public String modifyCenterPost(CenterDTO centerDTO) {
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
