package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String login() {
        return "/member/login";
    }

    @PostMapping("/login")
    public String loginPost() {
        return "/member/login";
    }

    @GetMapping("/main")
    public String main(Principal principal) {
        log.info(principal.getName());
        return "/member/main";
    }

    @GetMapping("/signup")
    public String signup() {
        return "/member/signup";
    }

    @PostMapping("/signup")
    public String signupPost(MemberDTO memberDTO) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberService.memberinsert(memberDTO);
        return "/member/signup";
    }


    //멤버 테이블 삭제 버튼
    @GetMapping("/delete")
    public String delteMember(Long memberNum) {
        log.info("member Controller에 도착 했지 :" + memberNum);

        memberService.memberDelet(memberNum);

        //다시 호텔룸 insert 창으로 보냄
        return "redirect:/hotelroom/insert";
    }


    //리스트 페이지 보여주기
    //todo:localhost:8090/member/list
    @GetMapping("/list")
    public String listMember(){

        return "member/lista";
    }



    //todo:localhost:8090/member/input
    /*리스트 페이지 컨트롤러 Member 리스트 페이지*/
    @GetMapping("/input")
    public String inputMemberGet(Model model,
                                      @PageableDefault(page=1) Pageable pageable

    ) {
        log.info("Getmapping 에 진입 했니 ");

        /*if (page < 0) {
            page = 0;  // 음수 방지
        }
        Pageable pageable = PageRequest.of(page, size);*/

        Page<MemberDTO> memberDTOS =
                memberService.memberList(pageable);

        model.addAttribute("memberDTOS", memberDTOS);

        return "member/insert";
    }


    //todo:localhost:8090/member/input
    //호텔룸 멤버 등록 페이지
    @PostMapping("/input")
    public String inputMemberGetPost(MemberDTO memberDTO,
                                         Model model)
    {
        log.info("호텔룸 등록 페이지에 들어 왔니:memberDTO를 가져와라 " + memberDTO);

        //view 보낸 member 등록을 저장
        memberService.memberinsert(memberDTO);

        //HotelRoom DB 에 값을 넣어 주는 메서드
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());

        return "member/insert";
    }

    //todo:http://localhost:8090/membersByHotelRoom/{hotelRoomNum}
    //todo:roomlist
    @PostMapping("/update/ajax")
    @ResponseBody
    public ResponseEntity<String> updateMemberAjax(@RequestBody MemberDTO memberDTO) {
        
        try {
            memberService.memberModify(memberDTO);  // 서비스 로직 내부에서 예외 처리 및 검증 수행
            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }
    }





}
