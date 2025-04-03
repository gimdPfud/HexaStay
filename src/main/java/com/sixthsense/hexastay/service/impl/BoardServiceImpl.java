package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.BoardDTO;
import com.sixthsense.hexastay.entity.Board;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.BoardRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.BoardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    //등록
    @Override
    public void boardInsert(BoardDTO boardDTO){
        Board board = modelMapper.map(boardDTO, Board.class);
        Member member =memberRepository.findById(boardDTO.getMemberNum()).orElseThrow();
        board.setMember(member);
        board = boardRepository.save(board);
        log.info("보드"+boardDTO.getMemberNum());
        boardRepository.save(board);
    }
    //목록
    @Override
    public Page<BoardDTO> boardList(Pageable pageable, Principal principal, String type, String keyword) {
        //String username = principal.getName(); // 로그인한 사용자 정보
        int currentPage = pageable.getPageNumber() - 1;
        int limits = 10;
        Pageable temp = PageRequest.of(currentPage, limits, Sort.Direction.DESC, "boardNum");

        Page<Board> boardList;

        // 검색 조건이 있을 경우 필터링
        if (keyword != null && !keyword.isEmpty()) {
            if ("S".equals(type)) {
                boardList = boardRepository.findByBoardTitleContaining(keyword, temp);
            } else if ("C".equals(type)) {
                boardList = boardRepository.findByBoardContentContaining(keyword, temp);
            } else if ("A".equals(type)) {
                boardList = boardRepository.findByBoardWriterContaining(keyword, temp);
            } else if ("SC".equals(type)) {
                boardList = boardRepository.findByBoardTitleContainingOrBoardContentContaining(keyword, keyword, temp);
            } else if ("SCA".equals(type)) {
                boardList = boardRepository.findByBoardWriterContainingOrBoardTitleContainingOrBoardContentContaining(keyword, keyword, keyword, temp);
            } else {
                boardList = boardRepository.findAll(temp); // 검색 조건이 없을 경우 전체 조회
            }
        } else {
            boardList = boardRepository.findAll(temp); // 검색 없이 전체 조회
        }
        Page<BoardDTO> BoardDTO = boardList.map(board -> modelMapper.map(board,BoardDTO.class));

        return BoardDTO;
    }

    //읽기
    @Override
    public BoardDTO boardRead(Long boardNum) {
        return null;
    }
    //수정
    @Override
    public void boardModify(BoardDTO boardDTO) {

    }

    //삭제
    @Override
    public void boardDelete(Long boardNum) {

    }





}
