package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Salaries;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.web.PageableDefault;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/settle")
public class SettleController {

    private final AdminService adminService;
    private final SettleService settleService;
    private final StoreService storeService;
    private final AdminRepository adminRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelRoomService hotelRoomService;
    private final SalariesService salariesService;

    @GetMapping("/chart")
    public String chart(Principal principal,
                        Model model,
                        @PageableDefault(size = 10) Pageable pageable,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        Long companyNum = adminRepository.findByAdminEmail(principal.getName()).getCompany().getCompanyNum();
        Page<RoomDTO> roomDTOList;

        if (startDate != null && endDate != null) {
            // 날짜 범위로 조회
            roomDTOList = settleService.getSettleListByDateRange(companyNum, startDate, endDate, pageable);
        } else {
            // 전체 조회
            roomDTOList = settleService.getSettleList(companyNum, pageable);
        }

        model.addAttribute("roomDTOList", roomDTOList);
        return "/settle/chart";
    }

    @GetMapping("/chart/load-more")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loadMore(Principal principal,
                                                        @PageableDefault(size = 10) Pageable pageable,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long companyNum = adminRepository.findByAdminEmail(principal.getName()).getCompany().getCompanyNum();
        Page<RoomDTO> roomDTOList;

        if (startDate != null && endDate != null) {
            roomDTOList = settleService.getSettleListByDateRange(companyNum, startDate, endDate, pageable);
        } else {
            roomDTOList = settleService.getSettleList(companyNum, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", roomDTOList.getContent());
        response.put("hasNext", roomDTOList.hasNext());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart/all-data")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getAllData(Principal principal,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long companyNum = adminRepository.findByAdminEmail(principal.getName()).getCompany().getCompanyNum();
        List<RoomDTO> allData;

        if (startDate != null && endDate != null) {
            allData = settleService.getAllSettleListByDateRange(companyNum, startDate, endDate);
        } else {
            allData = settleService.getAllSettleList(companyNum);
        }

        return ResponseEntity.ok(allData);
    }

    @GetMapping("/chartstore")
    public String chartStore(Principal principal,
                             Model model,
                             Pageable pageable,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        Long storeNum = adminRepository.findByAdminEmail(principal.getName()).getStore().getStoreNum();
        Page<OrderstoreDTO> orderstoreDTOList;

        if (startDate != null && endDate != null) {
            // 날짜 범위로 조회
            orderstoreDTOList = settleService.getSettleStoreListByDateRange(storeNum, startDate, endDate, pageable);
        } else {
            // 전체 조회
            orderstoreDTOList = settleService.getSettleStoreList(storeNum, pageable);
        }

        model.addAttribute("storeDTOList", orderstoreDTOList);
        return "/settle/chartstore";
    }
}
