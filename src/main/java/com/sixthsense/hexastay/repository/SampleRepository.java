/***********************************************
 * 인터페이스명 : SampleRepository
 * 기능 : 샘플 레포지토리입니다. findAll 쿼리는 아래와 같이 작성?
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27 repository생성, 김예령
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SampleRepository extends JpaRepository<Sample, Long> {

    @Query("select s from Sample s")
    Page<Sample> findAll (Pageable pageable);
}
