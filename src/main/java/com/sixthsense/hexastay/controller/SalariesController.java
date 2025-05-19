package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.service.SalariesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/salaries")
public class SalariesController {

    private final AdminRepository adminRepository;
    private final SalariesService salariesService;

    //급여용
    @GetMapping("/list")
    public String salaries(Pageable pageable, 
                           Principal principal, 
                           Model model,
                           @RequestParam(required = false) String startMonth,
                           @RequestParam(required = false) String endMonth) {
        Admin admin = adminRepository.findByAdminEmail(principal.getName());
        if (admin == null) {
            model.addAttribute("error", "직원 정보를 찾을 수 없습니다.");
            return "salaries/list";
        }

        // 월별 필터링 적용
        YearMonth startYearMonth = null;
        YearMonth endYearMonth = null;
        
        if (startMonth != null && !startMonth.isEmpty()) {
            startYearMonth = YearMonth.parse(startMonth);
        }
        
        if (endMonth != null && !endMonth.isEmpty()) {
            endYearMonth = YearMonth.parse(endMonth);
        }
        
        Page<SalariesDTO> salariesList;
        if (startYearMonth != null && endYearMonth != null) {
            salariesList = salariesService.getSalariesListByMonthRange(admin.getAdminEmail(), startYearMonth, endYearMonth, pageable);
        } else {
            salariesList = salariesService.getSalariesList(admin.getAdminEmail(), pageable);
        }
        
        model.addAttribute("salList", salariesList);
        return "salaries/list";
    }

    @GetMapping("/insert")
    public String insertSalaries(Principal principal, Pageable pageable, Model model) {
        String email = principal.getName();
        Page<AdminDTO> adminList = salariesService.getSalarAdminList(email, pageable);
        model.addAttribute("adminDTOList", adminList);
        return "salaries/insert";
    }

    @PostMapping("/insert")
    public String insertPost(SalariesDTO salariesDTO){
        salariesService.postSalaries(salariesDTO);
                return "redirect:/salaries/list";
    }

    // 스토어 관련 메서드
    @GetMapping("/store/list")
    @PreAuthorize("hasAnyRole('MGR', 'SUBMGR')")
    public String storeList(@RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate,
                          @PageableDefault(size = 10) Pageable pageable,
                          Model model) {
        Long storeNum = getCurrentStoreNum(); // 현재 로그인한 관리자의 스토어 번호
        
        Page<SalariesDTO> salariesList;
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
            salariesList = salariesService.getStoreSalariesListByDateRange(storeNum, start, end, pageable);
        } else {
            salariesList = salariesService.getStoreSalariesList(storeNum, pageable);
        }
        
        model.addAttribute("salariesList", salariesList);
        return "salaries/store_list";
    }

    @GetMapping("/store/register")
    @PreAuthorize("hasRole('MGR')")
    public String storeRegisterForm(Model model) {
        model.addAttribute("salariesDTO", new SalariesDTO());
        return "salaries/store_register";
    }

    @PostMapping("/store/register")
    @PreAuthorize("hasRole('MGR')")
    public String storeRegister(SalariesDTO salariesDTO, RedirectAttributes redirectAttributes) {
        Long storeNum = getCurrentStoreNum();
        salariesDTO.setStoreNum(storeNum);
        salariesService.registerStoreSalaries(salariesDTO);
        redirectAttributes.addFlashAttribute("msg", "급여가 등록되었습니다.");
        return "redirect:/salaries/store/list";
    }

    @GetMapping("/store/modify/{salariesNum}")
    @PreAuthorize("hasRole('MGR')")
    public String storeModifyForm(@PathVariable Long salariesNum, Model model) {
        SalariesDTO salariesDTO = salariesService.getStoreSalaries(salariesNum);
        model.addAttribute("salariesDTO", salariesDTO);
        return "salaries/store_modify";
    }

    @PostMapping("/store/modify")
    @PreAuthorize("hasRole('MGR')")
    public String storeModify(SalariesDTO salariesDTO, RedirectAttributes redirectAttributes) {
        salariesService.modifyStoreSalaries(salariesDTO);
        redirectAttributes.addFlashAttribute("msg", "급여가 수정되었습니다.");
        return "redirect:/salaries/store/list";
    }

    @PostMapping("/store/remove")
    @PreAuthorize("hasRole('MGR')")
    public String storeRemove(@RequestParam Long salariesNum, RedirectAttributes redirectAttributes) {
        salariesService.removeStoreSalaries(salariesNum);
        redirectAttributes.addFlashAttribute("msg", "급여가 삭제되었습니다.");
        return "redirect:/salaries/store/list";
    }

    // 현재 로그인한 관리자의 스토어 번호를 가져오는 메서드
    private Long getCurrentStoreNum() {
        // TODO: SecurityContext에서 현재 로그인한 관리자의 스토어 번호를 가져오는 로직 구현
        return null;
    }
}
