/***********************************************
 * 인터페이스명 : BoardRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Board;
import com.sixthsense.hexastay.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    //작성자로 조회
    Page<Board> findByBoardWriterContaining(String boardWriter, Pageable pageable);
    //제목으로 조회
    Page<Board> findByBoardTitleContaining(String boardTitle, Pageable pageable);
    //내용으로 조회
    Page<Board> findByBoardContentContaining(String boardContent, Pageable pageable);
    //제목+내용으로 조회
    Page<Board> findByBoardTitleContainingOrBoardContentContaining
    (String boardTitle,String boardContent, Pageable pageable);
    //제목+내용+작성자로 조회
    Page<Board> findByBoardWriterContainingOrBoardTitleContainingOrBoardContentContaining
    (String boardWriter,String boardTitle,String boardContent, Pageable pageable);



    //게시판 검색기능 확인
    @Query("SELECT b FROM Board b " +
            "WHERE b.boardTitle LIKE CONCAT('%', :keyword, '%') " +
            "OR b.boardContent LIKE CONCAT('%', :keyword, '%') " +
            "OR b.boardWriter LIKE CONCAT('%', :keyword, '%')")
    Page<Board> Boardsearch(@Param("keyword") String keyword, Pageable pageable);
    //Pageable member(Member member);
}
