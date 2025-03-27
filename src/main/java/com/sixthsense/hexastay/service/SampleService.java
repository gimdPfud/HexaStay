/***********************************************
 * 인터페이스명 : SampleService
 * 기능 : 샘플 서비스 인터페이스 입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.entity.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SampleService {

    /* sampleMethod : 페이저블을 받아서 해당하는 모든 샘플 페이지를 반환 */
    public Page<Sample> sampleMethod(Pageable sampleParam);
}
