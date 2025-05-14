/***********************************************
 * 인터페이스명 : StoremenuRepository
 * 기능 :
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-01 이름 변경 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.StoremenuOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoremenuOptionRepository extends JpaRepository<StoremenuOption, Long> {
    @Query("select a from StoremenuOption a")
    public Page<StoremenuOption> findAll(Pageable pageable);

    @Query("select s from StoremenuOption s where s.storemenu.storemenuNum=:storemenuNum and s.storemenuOptionStatus='alive'")
    public List<StoremenuOption> findAll(Long storemenuNum);

    @Query("select s from StoremenuOption s where s.storemenuOptionStatus=:storemenuOptionStatus and s.storemenu.storemenuNum=:storemenuNum")
    List<StoremenuOption> findByStatusAndMenuNum(String storemenuOptionStatus, Long storemenuNum);
}
