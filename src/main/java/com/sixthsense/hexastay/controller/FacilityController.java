package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.entity.Facility;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.repository.FacilityRepository;
import com.sixthsense.hexastay.service.FacilityService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Log4j2
@ToString
@RequestMapping("/facility")
public class FacilityController {

    private final FacilityService facilityService;
    private final CenterRepository centerRepository;

    @GetMapping("/list")
    public String listFacility(Pageable pageable, Model model) {
        log.info("get 방식 facility 목록 controller 진입");

        Page<FacilityDTO> facilityDTOS = facilityService.facilityList(pageable);

        model.addAttribute("facilityDTOS", facilityDTOS);

        return "facility/list";
    }

    @GetMapping("/signup")
    public String signupFacilityGet(Long centerNum, Model model, Pageable pageable) {
        log.info("get 방식 facility 등록 controller 진입");

        Page<Center> centerPage = centerRepository.findAll(pageable);
        model.addAttribute("centerPage", centerPage);

        return "facility/signup";
    }

    @PostMapping("/signup")
    public String signUpFacilityPost(FacilityDTO facilityDTO){
        log.info("post 방식 facility 등록 controller 진입");
        log.info("facilityDTO 갖고왔나요? : " + facilityDTO);
        log.info("CenterNum 갖고왔나요? : " + facilityDTO.getCenterNum());

        facilityService.facilityInsert(facilityDTO);

        return "redirect:/facility/list";
    }

    @GetMapping("/read/{facilityNum}")
    public String readFacility(@PathVariable("facilityNum") Long facilityNum, Model model){
        log.info("get 방식 facility 상세보기 controller 진입");

        FacilityDTO facilityDTO = facilityService.facilityRead(facilityNum);
        model.addAttribute("facilityDTO", facilityDTO);

        return "facility/read";
    }

    @GetMapping("/modify/{facilityNum}")
    public String modifyFacility(@PathVariable("facilityNum") Long facilityNum, Model model) {
        log.info("get 방식 facility 수정 controller 진입");

        if (facilityNum == null) {
            log.info("facilityNum을 찾을 수 없음");
            return "redirect:/facility/list";
        }

        FacilityDTO facilityDTO = facilityService.facilityRead(facilityNum);
        model.addAttribute("facilityDTO", facilityDTO);

        return "facility/modify";
    }

    @PostMapping("/modify")
    public String modifyFacilityPost(FacilityDTO facilityDTO){
        log.info("Post 방식 Facility 수정 controller 진입");
        facilityService.facilityInsert(facilityDTO);

        return "redirect:/facility/list";
    }

    @PostMapping("/delete/{facilityNum}")
    public ResponseEntity<String> deleteBranch(@PathVariable("branchNum") Long branchNum){
        log.info("Post 방식 branch 삭제 controller 진입");

        try {
            facilityService.facilityDelete(branchNum);

            return ResponseEntity.ok("삭제 성공");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패 다시 시도해주세요");
        }
    }


    // 조직등록 - 지점

    @PostMapping("/facilityinsert")
    @ResponseBody
    public ResponseEntity<Void> facilityInsertPost(@RequestBody FacilityDTO facilityDTO) {
        log.info(facilityDTO.toString());
        facilityService.facilityInsert(facilityDTO);
        return ResponseEntity.ok().build();
    }



}
