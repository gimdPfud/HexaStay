package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.HotelRoomDTO;

import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.HotelRoomService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

import java.util.Map;



@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/hotelroom")
public class HotelRoomController {


    //소속 가져오기
    private final AdminService adminService;
    private final CompanyService companyService;

    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;



    // roomlist.html - checkin , checkout 변경용 로직 <script>
    @PostMapping("/checkinout/{hotelRoomNum}")
    @ResponseBody
    public ResponseEntity<?> checkInOut(@PathVariable Long hotelRoomNum,
                                        @RequestBody Map<String, String> body) {
        String status = body.get("status");
        log.info("호텔룸 번호: {}, 상태: {}", hotelRoomNum, status);

        try {
            hotelRoomService.checkInOut(hotelRoomNum, status);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("호텔룸을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("체크인/아웃 처리 중 예외 발생", e);
            return ResponseEntity.internalServerError().build();
        }

    }


    //호텔룸 등록페이지

    //todo:http://localhost:8090/admin/hotelroom/input
    @GetMapping("/input")
    public String inputHotelRoomGet(Model model, Principal principal) {
        String loginEmail = principal.getName();
        AdminDTO adminDTO = adminService.adminFindEmail(loginEmail);


        //여기에 소속되어 있는 모든 리스트 정보 가져 오기
        Long companyNum =  adminDTO.getCompanyNum();
        CompanyDTO companyDTO = companyService.companyRead(companyNum);

        model.addAttribute("comapny", companyDTO);

        log.info("Get 등록 페이지 진입");

        return "hotelroom/inputhotelroom";
    }

    //todo:http://localhost:8090/admin/hotelroom/input
    @PostMapping("/input")
    public String inputHotelRoomPost(HotelRoomDTO hotelRoomDTO,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal)
    {
        log.info("호텔룸 등록 요청: {}", hotelRoomDTO);
        String loginEmail = principal.getName();
        Long companyNum = adminService.adminFindEmail(loginEmail).getCompanyNum();

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
            hotelRoomService.hotelroomInsert(hotelRoomDTO,companyNum);

            redirectAttributes.addFlashAttribute("message", "호텔룸이 성공적으로 등록되었습니다.");
            return "redirect:/admin/hotelroom/input";
        } catch (Exception e) {
            log.error("호텔룸 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "호텔룸 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/admin/hotelroom/input";
        }
    }

    // list.html - 객실 정보 리스트 페이지
    @GetMapping("/list")
    public String hotelRoomList(Model model, Principal principal,
                                @RequestParam(value = "page", defaultValue = "0") int page)
    {

        System.out.println("✅ hotelRoomList Controller 호출됨");
        log.info("✅ hotelRoomList Controller 호출됨");

        Pageable pageable = PageRequest.of(page, 9,
                Sort.by(Sort.Order.desc("hotelRoomNum"), Sort.Order.desc("createDate"))
        );
        //페이지 사이즈 체크 로그
        log.info("pageable : page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());


        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());

        Page<HotelRoomDTO> hotelRoomList = hotelRoomService.hotelroomList(pageable);

        model.addAttribute("companylist", hotelRoomService.listCompany(adminDTO.getCompanyNum()));
        model.addAttribute("hotelRoomList", hotelRoomList);

        // ✔ Principal email 그대로 넘기기
        model.addAttribute("adminEmail", principal.getName());

        return "hotelroom/list";
    }

    //todo:/hotelRoomsByMember/{memberNum}
    //모달 페이지 수정 하기
    //modal창에서 호텔룸 수정 페이지
    @PostMapping("/update")
    public String hotelRoomUpdatePost(@RequestParam Long hotelRoomNum,
                                      HotelRoomDTO hotelRoomDTO,
        RedirectAttributes redirectAttributes,Principal principal                              )
    {
        String loginEmail = principal.getName();
        Long companyNum = adminService.adminFindEmail(loginEmail).getCompanyNum();


        log.info(hotelRoomNum + "hotelRoomNum Post 페이지에 들어 오기는 했지 ");
        log.info(hotelRoomNum + "hotelRoomNum 키 값이 들어 오니  ");

        try {
            hotelRoomService.hotelroomUpdate(hotelRoomNum,hotelRoomDTO,companyNum);
            redirectAttributes.addFlashAttribute("successMessage", "호텔룸 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "호텔룸 수정 중 오류가 발생했습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/roomlist";
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


    //호텔룸 수정 페이지
    @GetMapping("/modify")
    public String hotelRoomModifyGet(@RequestParam("hotelRoomNum") Long hotelRoomNum, Model model) {
        HotelRoomDTO hotelRoomDTO = hotelRoomService.hotelroomrRead(hotelRoomNum);
        model.addAttribute("hotelRoomDTO", hotelRoomDTO);
        return "hotelroom/modifyhotelroom";
    }

    @PostMapping("/modify")
    public String hotelRoomModifyPost(
            @RequestParam(value = "hotelRoomNum", required = false) Long hotelRoomNum,
            @ModelAttribute HotelRoomDTO hotelRoomDTO,
            Model model,
            Principal principal) throws IOException
    {
        String loginEmail = principal.getName();
        Long companyNum = adminService.adminFindEmail(loginEmail).getCompanyNum();
        log.info(hotelRoomNum + "수정 modidyfy 페이지 에는 들어 오기는 했냐 ");


        // hotelroomnum 값이 없는 경우 예외 처리
        if (hotelRoomNum == null) {
            model.addAttribute("errorMessage", "호텔룸 번호가 전달되지 않았습니다.");
            return "hotelroom/modifyhotelroom"; // 또는 에러 페이지
        }

        try {
            hotelRoomService.hotelroomUpdate(hotelRoomNum, hotelRoomDTO,companyNum);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "호텔룸 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "hotelroom/modifyhotelroom";
        }

        return "redirect:/admin/hotelroom/list"; // 수정 후 목록 페이지로 이동하거나 필요에 맞게 수정
    }

    //회원 일시 룸배정 객실 정보 리스트로 보여주는 메소드
    //todo:http://localhost:8090/register-hotelroom
    @GetMapping("/listpage")
    @ResponseBody
    public ResponseEntity<Page<HotelRoomDTO>> getHotelRoomList(Pageable pageable) {
        Page<HotelRoomDTO> rooms = hotelRoomService.hotelroomList(pageable); // 이미지 포함한 DTO 반환
        return ResponseEntity.ok(rooms);
    }



}
