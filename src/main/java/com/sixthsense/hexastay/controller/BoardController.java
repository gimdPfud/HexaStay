package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.BoardDTO;
import com.sixthsense.hexastay.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final ModelMapper modelMapper = new ModelMapper();
    //등록
    @GetMapping("/insert")
    public String insert (Model model) {
        log.info("보드");
        model.addAttribute("memberNum");
        log.info("insert into board");
        return "/board/insert";
    }
    @PostMapping("/insert")
    public String insertPost(BoardDTO boardDTO) {
        log.info("포스트:"+boardDTO);
        boardService.boardInsert(boardDTO);
        return "redirect:/board/list?";
    }
    //목록
    @GetMapping(value = {"/","/list"})
    public String list(
            @PageableDefault(page=1) Pageable pageable,
            @RequestParam(value = "type",defaultValue = "") String type,
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            Model model, Principal principal){
        //서비스 연동
        Page<BoardDTO> listDTOS = boardService.boardList(pageable,principal,type, keyword);
        //페이지 정보 가공
        Map<String,Integer> pageInfo= Pagination(listDTOS);
        //값 전달(model)
        model.addAttribute("list", listDTOS);
        //조회정보전달
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        //페이지 정보 전달
        model.addAllAttributes(pageInfo);
        return "/board/list";
    }


}
