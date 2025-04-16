package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.HotelRoomDTO;

import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import com.sixthsense.hexastay.service.impl.QrCodeServiceimpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.NoSuchElementException;


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

    private final QrCodeServiceimpl qrCodeServiceimpl;


    //0411
    @GetMapping("/latest")
    @ResponseBody
    public ResponseEntity<HotelRoomDTO> getLatestRoomByName(@RequestParam String hotelRoomName) {
        try {
            HotelRoomDTO hotelRoomDTO = hotelRoomService.HotelRoomByName(hotelRoomName);
            return ResponseEntity.ok(hotelRoomDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
    public String inputHotelRoomPost(HotelRoomDTO hotelRoomDTO, RedirectAttributes redirectAttributes) {
        log.info("호텔룸 등록 요청: {}", hotelRoomDTO);

        if (hotelRoomDTO == null) {
            redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
            return "redirect:/admin/hotelroom/input";
        }

        try {
            // 호텔룸 상세 페이지 URL 설정 (예: 방 번호 기반 URL)
            String qrContent = "/hotel/room/details/" + hotelRoomDTO.getHotelRoomNum();  // QR 코드로 변환할 URL

            // URL을 호텔룸 QR 코드 필드에 설정
            hotelRoomDTO.setHotelRoomQr(qrContent);  // URL을 hotelRoomQr에 설정

            // 호텔룸 저장
            hotelRoomService.hotelroomInsert(hotelRoomDTO);

            redirectAttributes.addFlashAttribute("message", "호텔룸이 성공적으로 등록되었습니다.");
            return "redirect:/admin/hotelroom/input";
        } catch (Exception e) {
            log.error("호텔룸 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "호텔룸 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/admin/hotelroom/input";
        }
    }

    @GetMapping("list")
    public String hotelRoomList(Model model,Principal principal,
                                HotelRoomDTO hotelRoomDTO,
          @PageableDefault(page = 1, size = 10, sort = "companyNum", direction = Sort.Direction.DESC) Pageable pageable

    )
    {

        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());

        Page<HotelRoomDTO> hotelRoomList = hotelRoomService.hotelroomList( pageable);

        model.addAttribute("companylist", hotelRoomService.listCompany(adminDTO.getCompanyNum()));
        model.addAttribute("hotelRoomList", hotelRoomList);


        // 혹시 페이징 처리된 호텔 룸 리스트도 넘기고 싶다면 아래처럼:
        // Page<HotelRoomDTO> hotelRoomList = hotelRoomService.getHotelRooms(adminDTO.getCompanyNum(), pageable);
        // model.addAttribute("hotelRoomList", hotelRoomList);


        return "hotelroom/list";
    }

    //todo:/hotelRoomsByMember/{memberNum}
    //모달 페이지 수정 하기
    @PostMapping("update")
    public String hotelRoomUpdatePost(@ModelAttribute HotelRoomDTO hotelRoomDTO,
        RedirectAttributes redirectAttributes                              ) {

        log.info("hotelRoomUpdate Post 페이지에 들어 오기는 했지 ");

        try {
            hotelRoomService.hotelroomrModify(hotelRoomDTO);
            redirectAttributes.addFlashAttribute("successMessage", "호텔룸 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "호텔룸 수정 중 오류가 발생했습니다.");
        }

        return "redirect:/hotelRoomsByMember/" + hotelRoomDTO.getMemberNum();
    }

    // 호텔룸 상세 페이지로 이동
    @GetMapping("details/{hotelRoomNum}")
    public String hotelRoomDetails(@PathVariable Long hotelRoomNum, Model model) {
        log.info("호텔룸 상세 페이지 요청: {}", hotelRoomNum);

        // 해당 호텔룸 정보 조회
        HotelRoomDTO hotelRoomDTO = hotelRoomService.hotelroomrRead(hotelRoomNum);

        // 상세 페이지에서 사용할 데이터를 모델에 추가
        model.addAttribute("hotelRoom", hotelRoomDTO);

        // 호텔룸 상세 페이지로 이동
        return "hotelroom/hotelRoomDetails"; // 호텔룸 상세 정보를 보여줄 HTML 페이지 이름
    }























}
