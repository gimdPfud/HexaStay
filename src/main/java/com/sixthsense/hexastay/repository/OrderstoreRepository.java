/***********************************************
 * 인터페이스명 : OrderstoreRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Orderstore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderstoreRepository extends JpaRepository<Orderstore, Long> {
    /*다가져오기*/
    @Query("select a from Orderstore a")
    public Page<Orderstore> findAll(Pageable pageable);

    /*페이지로 가져오기*/
    Page<Orderstore> findByMember_MemberEmail (String email, Pageable pageable);
    /*리스트로 가져오기. 근데 주문내역이면........... 계속 나오지않나*/
    List<Orderstore> findByMember_MemberEmail (String email);

}