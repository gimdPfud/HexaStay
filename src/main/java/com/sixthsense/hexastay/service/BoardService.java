package com.sixthsense.hexastay.service;
import com.sixthsense.hexastay.dto.BoardDTO;
import com.sixthsense.hexastay.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.security.Principal;

public interface BoardService {
    //등록
    public void boardInsert(BoardDTO boardDTO,Long memberNum);
    //목록
    public Page<BoardDTO> boardList(Pageable pageable, Principal principal ,String type, String keyword);
    //읽기
    public BoardDTO boardRead(Long boardNum);
    //수정
    public void boardModify(BoardDTO boardDTO);
    //삭제
    public void boardDelete(Long boardNum);
}
