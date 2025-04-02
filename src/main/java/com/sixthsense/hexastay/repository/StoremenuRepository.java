/***********************************************
 * 인터페이스명 : StoremenuRepository
 * 기능 :
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-01 이름 변경 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Storemenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoremenuRepository extends JpaRepository<Storemenu, Long> {
    @Query("select a from Storemenu a")
    public Page<Storemenu> findAll(Pageable pageable);


    /*활성화여부와 상위 가게 pk로...*/
    Page<Storemenu> findByStoreStoreNumAndStoremenuStatus(Long storeNum, String storemenuStatus, Pageable pageable);
}
