/***********************************************
 * 인터페이스명 : FacilitiesRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilitiesRepository extends JpaRepository<Facilities, Long> {
}