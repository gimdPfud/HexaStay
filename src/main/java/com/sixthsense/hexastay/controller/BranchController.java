package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String signUpBranchPost(BranchDTO branchDTO) throws IOException {
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

        return "center/read";
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
    public ResponseEntity<Void> branchInsertPost(@RequestBody BranchDTO branchDTO, MultipartFile companyPicture) throws IOException {
        branchDTO.setCompanyPicture(companyPicture);
        log.info("아옿" + branchDTO.getCompanyPicture().getOriginalFilename());
        branchService.branchInsert(branchDTO);
        return ResponseEntity.ok().build();
    }


}
