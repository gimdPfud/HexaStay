package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.ReviewDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    // (1) 리뷰 리스트 페이지
    @GetMapping("/list")
    public String reviewList(Model model, Pageable pageable, Principal principal) {
        log.info("리뷰 리스트 진입");

        Page<ReviewDTO> result = reviewService.reviewList(pageable);
        result.forEach(log::info);
        model.addAttribute("reviewList", result);

        List<ReviewDTO> bestList = reviewService.reviewBest();
        model.addAttribute("bestList", bestList);

        // 현재 로그인된 회원 번호 전달 (좋아요 제어용)
        if (principal != null) {
            model.addAttribute("memberNum", principal.getName());
        }

        return "review/list";
    }

    // (2) 리뷰 등록 폼
    @GetMapping("/insert")
    public String reviewInsertForm(Model model, Principal principal) {
        log.info("리뷰 작성 페이지 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("memberId", principal.getName());
        return "review/insert";
    }

    // (3) 리뷰 등록 처리
    @PostMapping("/insert")
    public String reviewInsert(@ModelAttribute ReviewDTO reviewDTO, Principal principal) {
        log.info("리뷰 등록 처리 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        reviewDTO.setMemberNum(Long.parseLong(principal.getName()));
        reviewService.reviewInsert(reviewDTO);

        return "redirect:/review/list";
    }

    // (4) (수정) 좋아요 처리
    @PostMapping("/good/{reviewNum}")
    public String reviewGood(@PathVariable Long reviewNum, Principal principal) {
        log.info("리뷰 좋아요 처리 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        Long memberNum = Long.parseLong(principal.getName());
        reviewService.reviewGood(reviewNum, memberNum);

        return "redirect:/review/list";
    }

    // (5) 리뷰 수정 폼
    @GetMapping("/modify/{reviewNum}")
    public String reviewModifyForm(@PathVariable Long reviewNum, Model model, Principal principal) {
        log.info("리뷰 수정 페이지 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        ReviewDTO reviewDTO = reviewService.findById(reviewNum);
        if (!principal.getName().equals(String.valueOf(reviewDTO.getMemberNum()))) {
            log.info("본인이 작성한 글이 아님");
            return "redirect:/review/list";
        }

        model.addAttribute("reviewDTO", reviewDTO);
        return "review/insert";
    }

    // (6) 리뷰 수정 처리
    @PostMapping("/modify")
    public String reviewModify(@ModelAttribute ReviewDTO reviewDTO, Principal principal) {
        log.info("리뷰 수정 처리 진입");

        if (principal == null || !principal.getName().equals(String.valueOf(reviewDTO.getMemberNum()))) {
            return "redirect:/review/list";
        }

        reviewService.reviewModify(reviewDTO);
        return "redirect:/review/list";
    }

    // (7) 리뷰 삭제 처리
    @PostMapping("/delete/{reviewNum}")
    public String reviewDelete(@PathVariable Long reviewNum, Principal principal) {
        log.info("리뷰 삭제 처리 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        ReviewDTO reviewDTO = reviewService.findById(reviewNum);
        if (!principal.getName().equals(String.valueOf(reviewDTO.getMemberNum()))) {
            log.info("삭제 권한 없음");
            return "redirect:/review/list";
        }

        reviewService.reviewDelete(reviewNum);
        return "redirect:/review/list";
    }

    // (8) 별점 평균
    @ResponseBody
    @GetMapping("/ratingAvg/{hotelRoomNum}")
    public Double reviewRatingAvg(@PathVariable Long hotelRoomNum) {
        log.info("별점 평균 조회");
        return reviewService.reviewRatingAvg(hotelRoomNum);
    }
}
