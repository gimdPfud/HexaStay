/***********************************************
 * 클래스명 : StoreLikeRepository
 * 기능 :
 * 작성자 : 김예령
 * 작성일 : 2025-04-22
 * 수정 : 2025-04-22
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {

//    long countByStore_StoreNumAndStoreLikeStatus(Long storeNum, String storeLikeStatus);
    long countByStore_StoreNum(Long storeNum);

    StoreLike findByMember_MemberEmailAndStore_StoreNum(String memberEmail, Long storeNum);
    StoreLike findByMemberAndStore_StoreNum(Member member, Long storeNum);

    boolean existsByStore_StoreNumAndMember_MemberEmail(Long storeNum, String memberEmail);
    boolean existsByStore_StoreNumAndMember(Long storeNum, Member member);
}