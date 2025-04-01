package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.commCategory = :commCategory AND c.commContentNum = :commContentNum")
    Page<Comment> getAllCommentWithMember(@Param("commCategory") Integer commCategory, @Param("commContentNum") Long commContentNum, Pageable pageable);


    Page<Comment> findByCommCategoryAndCommContentNum(Integer commCategory, Long commContentNum, Pageable pageable);

}
