/***********************************************
 * 클래스명 : SampleServiceImpl
 * 기능 : 샘플 서비스 임플입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.SampleDTO;
import com.sixthsense.hexastay.entity.Sample;
import com.sixthsense.hexastay.repository.SampleRepository;
import com.sixthsense.hexastay.service.SampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SampleServiceImpl implements SampleService {
    private final SampleRepository sampleRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<SampleDTO> sampleMethod(Pageable sampleParam) {
        Page<Sample> samplePage = sampleRepository.findAll(sampleParam);
        Page<SampleDTO> sampleResult = samplePage.map(data->modelMapper.map(data,SampleDTO.class));
        return sampleResult;
    }
}
