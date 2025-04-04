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
            if (select.equals("company")) {
                if (choice.equals("center")) {
                    Page<CenterDTO> centerDTOList = centerService.companyName(keyword, pageable);
                    model.addAttribute("centerDTOS", centerDTOList);
                } else if (choice.equals("branch")) {
                    Page<BranchDTO> branchDTOList = branchService.companyName(keyword, pageable);
                    model.addAttribute("branchDTOS", branchDTOList);
                } else if (choice.equals("facility")) {
                    Page<FacilityDTO> facilityDTOList = facilityService.companyName(keyword, pageable);
                    model.addAttribute("facilityDTOS", facilityDTOList);
                }

            } else if (select.equals("brandName")) {
                if (choice.equals("center")) {
                    Page<CenterDTO> centerDTOList = centerService.brandName(keyword, pageable);
                    model.addAttribute("centerDTOS", centerDTOList);
                } else if (choice.equals("branch")) {
                    Page<BranchDTO> branchDTOList = branchService.brandName(keyword, pageable);
                    model.addAttribute("branchDTOS", branchDTOList);
                } else if (choice.equals("facility")) {
                    Page<FacilityDTO> facilityDTOList = facilityService.brandName(keyword, pageable);
                    model.addAttribute("facilityDTOS", facilityDTOList);
                }


                //브랜치 기준 --> 키워드를 가지고 -> 센터쪽에서 해당 브랜드 이름을 가지고 있는 센터Num 찾는다. ->
                //그 센터Num을 가지고 브렌치에서 FK에 해당 센터넘을 가지고 있는놈들을 뽑는다



            } else if (select.equals("businessNum")) {
                if (choice.equals("center")) {
                    Page<CenterDTO> centerDTOList = centerService.companyName(keyword, pageable);
                    model.addAttribute("centerDTOS", centerDTOList);
                } else if (choice.equals("branch")) {
                    Page<BranchDTO> branchDTOList = branchService.companyName(keyword, pageable);
                    model.addAttribute("branchDTOS", branchDTOList);
                } else if (choice.equals("facility")) {
                    Page<FacilityDTO> facilityDTOList = facilityService.companyName(keyword, pageable);
                    model.addAttribute("facilityDTOS", facilityDTOList);
                }
            }
        } else {
            log.info("눌렀당");
            Page<CenterDTO> centerDTOList = centerService.centerList(pageable);
            model.addAttribute("centerDTOS", centerDTOList);
        }
        return "center/list"; // 무조건 템플릿 리턴!
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
