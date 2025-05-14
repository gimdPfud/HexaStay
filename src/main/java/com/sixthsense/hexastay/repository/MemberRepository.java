/***********************************************
 * 인터페이스명 : MemberRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select a from Member a")
    public Page<Member> findAll(Pageable pageable);

    Member findByMemberEmail(String memberEmail);

    //member의 이름과 email을 찾아 오는 검색 커리
    @Query("SELECT m FROM Member m WHERE m.memberName LIKE %:keyword% OR m.memberEmail LIKE %:keyword%")
    List<Member> searchByNameOrEmail(@Param("keyword") String keyword);

    // 쿠폰을 발급 받기 위한 이메일 유효성 검사.
    boolean existsByMemberEmail(String email);






}
