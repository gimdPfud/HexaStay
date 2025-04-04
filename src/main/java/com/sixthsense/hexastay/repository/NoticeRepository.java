package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 작성자로 조회
    Page<Notice> findByNoticeWriterContaining(String noticeWriter, Pageable pageable);

    // 제목으로 조회
    Page<Notice> findByNoticeTitleContaining(String noticeTitle, Pageable pageable);

    // 내용으로 조회
    Page<Notice> findByNoticeContentContaining(String noticeContent, Pageable pageable);

    // 제목+내용으로 조회
    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining
    (String noticeTitle, String noticeContent, Pageable pageable);

    // 제목+내용+작성자로 조회
    Page<Notice> findByNoticeWriterContainingOrNoticeTitleContainingOrNoticeContentContaining
    (String noticeWriter, String noticeTitle, String noticeContent, Pageable pageable);

    // 게시판 검색 기능 확인
    @Query("SELECT b FROM Notice b " +
            "WHERE b.noticeTitle LIKE CONCAT('%', :keyword, '%') " +
            "OR b.noticeContent LIKE CONCAT('%', :keyword, '%') " +
            "OR b.noticeWriter LIKE CONCAT('%', :keyword, '%')")
    Page<Notice> Noticesearch(@Param("keyword") String keyword, Pageable pageable);;

}
