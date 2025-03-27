/***********************************************
 * 클래스명 : SampleServiceImpl
 * 기능 : 샘플 서비스 임플입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.entity.Sample;
import com.sixthsense.hexastay.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService{
    private final SampleRepository sampleRepository;

    @Override
    public Page<Sample> sampleMethod(Pageable sampleParam) {
        Page<Sample> sampleResult = sampleRepository.findAll(sampleParam);
        return sampleResult;
    }
}
