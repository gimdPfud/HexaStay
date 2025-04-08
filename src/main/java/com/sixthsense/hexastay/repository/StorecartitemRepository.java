/***********************************************
 * 인터페이스명 : StorecartitemRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Storecartitem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorecartitemRepository extends JpaRepository<Storecartitem, Long> {

    //장바구니에 상품이 있는지 확인하기.
    Storecartitem findByStorecart_StorecartNumAndStoremenu_StoremenuNum(Long cartNum, Long menuNum);
}