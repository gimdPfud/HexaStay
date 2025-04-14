package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/hotelroom")
public class HotelRoomController {

    //member 서비스 가져오기
    private final MemberService memberService;
    private final AdminService adminService;
    private final CompanyService companyService;

    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;


    //0411
    @GetMapping("/latest")
    @ResponseBody
    public ResponseEntity<HotelRoomDTO> getLatestHotelRoom(@RequestParam String roomType) {
        HotelRoomDTO hotelRoomDTO = hotelRoomService.getLatestHotelRoomByType(roomType);
        log.info(hotelRoomDTO.toString() + "dkdkdlkfjlkdsjdlfkjlkdjfdlkjlk");
        return hotelRoomDTO != null ? ResponseEntity.ok(hotelRoomDTO) : ResponseEntity.notFound().build();
    }


    //호텔룸 등록

    //호텔룸 등록페이지

    //todo:http://localhost:8090/admin/hotelroom/input
    @GetMapping("/input")
    public String inputHotelRoomGet(Model model, Principal principal) {
        String loginEmail = principal.getName();
        AdminDTO adminDTO = adminService.adminFindEmail(loginEmail);

        //여기에 소속되어 있는 보든 리스트 정보 가져 오기
        Long companyNum =  adminDTO.getCompanyNum();
        CompanyDTO companyDTO = companyService.companyRead(companyNum);

        model.addAttribute("comapny", companyDTO);

        log.info("Get 등록 페이지 진입");

        return "hotelroom/inputhotelroom";
    }

    //todo:http://localhost:8090/admin/hotelroom/input
    @PostMapping("/input")
    public String inputHotelRoomPost( HotelRoomDTO hotelRoomDTO,
                                     RedirectAttributes redirectAttributes
                                      ) throws IOException {
        log.info("컴퍼니넘컴퍼니넘컴퍼니넘" + hotelRoomDTO.getCompanyNum());



        if (hotelRoomDTO == null) {
            redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
            return "redirect:/admin/hotelroom/input";
        }

        log.info("호텔룸 등록 요청: {}", hotelRoomDTO);
        hotelRoomService.hotelroomInsert(hotelRoomDTO);

        redirectAttributes.addFlashAttribute("message", "호텔룸이 성공적으로 등록되었습니다.");
        return "redirect:/admin/hotelroom/input"; // 등록 후 목록으로 이동
    }

    @GetMapping("list")
    public String hotelRoomList(Model model,Principal principal,
                                HotelRoomDTO hotelRoomDTO) {

        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());

        model.addAttribute("companylist", hotelRoomService.listCompany(adminDTO.getCompanyNum()));


        return "hotelroom/list";
    }


















}
