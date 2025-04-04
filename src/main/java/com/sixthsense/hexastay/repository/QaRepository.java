package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Qa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QaRepository extends JpaRepository<Qa, Long> {

    // 작성자로 조회
    Page<Qa> findByQaWriterContaining(String qaWriter, Pageable pageable);

    // 제목으로 조회
    Page<Qa> findByQaTitleContaining(String qaTitle, Pageable pageable);

    // 내용으로 조회
    Page<Qa> findByQaContentContaining(String qaContent, Pageable pageable);

    // 제목+내용으로 조회
    Page<Qa> findByQaTitleContainingOrQaContentContaining
    (String qaTitle, String qaContent, Pageable pageable);

    // 제목+내용+작성자로 조회
    Page<Qa> findByQaWriterContainingOrQaTitleContainingOrQaContentContaining
   (String qaWriter, String qaTitle, String qaContent, Pageable pageable);

    // 게시판 검색 기능 확인
    @Query("SELECT q FROM Qa q WHERE " +
            "(:type = 'T' AND q.qaTitle LIKE %:keyword%) OR " +
            "(:type = 'C' AND q.qaContent LIKE %:keyword%) OR " +
            "(:type = 'W' AND q.qaWriter LIKE %:keyword%)")
    Page<Qa> search(@Param("type") String type,
                    @Param("keyword") String keyword,
                    Pageable pageable);

}
