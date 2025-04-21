package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/chart")
    public String chart(Principal principal, Model model, Pageable pageable)
    {

        Long companyNum = adminRepository.findByAdminEmail(principal.getName()).getCompany().getCompanyNum();
        Page<RoomDTO> roomDTOList = settleService.getSettleList(companyNum, pageable);
        model.addAttribute("roomDTOList", roomDTOList);
        return "/settle/chart";
    }

    @GetMapping("/chartstore")
    public String chartStore(Principal principal, Model model, Pageable pageable)
    {

        Long storeNum = adminRepository.findByAdminEmail(principal.getName()).getStore().getStoreNum();
        Page<OrderstoreDTO> orderstoreDTOList = settleService.getSettleStoreList(storeNum, pageable);
        model.addAttribute("storeDTOList", orderstoreDTOList);
        return "/settle/chartstore";
    }

    @GetMapping("/salaries")
    public String salaries() {
        return "/settle/salaries";
    }



}
