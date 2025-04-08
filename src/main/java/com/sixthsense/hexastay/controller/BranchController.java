package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.FacilityDTO;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.service.BranchService;
import com.sixthsense.hexastay.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
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
@RequestMapping("/branch")
public class BranchController {

    private final CenterService centerService;
    private final BranchService branchService;
    private final CenterRepository centerRepository;

    @GetMapping("/list")
    public String listBranch(Pageable pageable, Model model){
        log.info("get 방식 branch 목록 controller 진입");

        Page<BranchDTO> branchDTOS = branchService.branchList(pageable);

        model.addAttribute("branchDTOS", branchDTOS);

        return "branch/list";
    }

    @GetMapping("/signup")
    public String signUpBranchGet(Long centerNum, Model model, Pageable pageable){
        log.info("get 방식 branch 등록 controller 진입");

        Page<Center> centerPage = centerRepository.findAll(pageable);
        model.addAttribute("centerPage", centerPage);

        return "branch/signUp";
    }

    @PostMapping("/signup")
    public String signUpBranchPost(BranchDTO branchDTO){
        log.info("post 방식 branch 등록 controller 진입");
        log.info("branchDTO 갖고왔나요? : " + branchDTO);
        log.info("CenterNum 갖고왔나요? : " + branchDTO.getCenterNum());

        branchService.branchInsert(branchDTO);

        return "redirect:/center/list";
    }

    @GetMapping("/read/{branchNum}")
    public String readBranch(@PathVariable("branchNum") Long branchNum, Model model) {
        log.info("get 방식 branch 상세보기 controller 진입");

        BranchDTO branchDTO = branchService.branchRead(branchNum);
        model.addAttribute("branchDTO", branchDTO);

        return "branch/read";
    }

    @GetMapping("/modify/{branchNum}")
    public String modifyBranch(@PathVariable("branchNum") Long branchNum, Model model){
        log.info("get 방식 branch 수정 controller 진입");

        if(branchNum == null){
            log.info("branchNum을 찾을 수 없음");
            return "redirect:/center/list";
        }

        BranchDTO branchDTO = branchService.branchRead(branchNum);
        //브랜드명, 본사명 리스트 가져오기
        List<String> centerBrandList = centerRepository.findDistinctCenterBrand();
        List<String> centerNameList = centerRepository.findDistinctCenterName();
        log.info("centerBrandList 갖고 왔나? : " + centerBrandList );
        log.info("centerNameList 갖고 왔나? : " + centerNameList );

        model.addAttribute("branchDTO", branchDTO);
        model.addAttribute("centerBrandList", centerBrandList);
        model.addAttribute("centerNameList", centerNameList);

        return "branch/modify";
    }

    @PostMapping("/modify/{branchNum}")
    public String modifyBranchPost(@ModelAttribute BranchDTO branchDTO){
        log.info("Post 방식 branch 수정 controller 진입");
        branchService.branchModify(branchDTO);

        return "redirect:/center/list";
    }

    @PostMapping("/delete/{branchNum}")
    public ResponseEntity<String> deleteBranch(@PathVariable("branchNum") Long branchNum){
        log.info("Post 방식 branch 삭제 controller 진입");

        try {
            branchService.branchDelete(branchNum);

            return ResponseEntity.ok("삭제 성공");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패 다시 시도해주세요");
        }
    }

    // 조직등록 - 지사
    @PostMapping("/branchinsert")
    @ResponseBody
    public ResponseEntity<Void> branchInsertPost(@RequestBody BranchDTO branchDTO) {
        log.info(branchDTO.toString());
        branchService.branchInsert(branchDTO);
        return ResponseEntity.ok().build();
    }


}
