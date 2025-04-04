package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.QaDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Qa;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.QaRepository;
import com.sixthsense.hexastay.service.QaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class QaServiceImpl implements QaService {
    private final QaRepository qaRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void qaInsert(QaDTO qaDTO) {
        // 임시 작성자 지정 (하드코딩된 값)
        qaDTO.setMemberNum(1L);           // TODO: 실제 로그인한 멤버 ID로 대체 예정

        // (1) 여기서 멤버 조회 및 예외 처리
        Member memberOpt = memberRepository.findById(qaDTO.getMemberNum())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // (2) DTO → Entity 변환
        Qa qa = Qa.builder()
                .qaTitle(qaDTO.getQaTitle())
                .qaContent(qaDTO.getQaContent())
                .qaWriter(memberOpt.getMemberName())
                .qaAnswered(false) // (추가) 기본값 false 설정
                .member(memberOpt)
                .build();

        // (3) 저장 및 로깅
        log.info("Qa 저장 전: {}", qa.toString());
        qaRepository.save(qa); // 중복 저장 제거
        log.info("Qa 저장 완료. 작성자 ID: {}", memberOpt.getMemberNum()               );
    }
    //목록
    @Override
    public Page<QaDTO> qaList(Pageable pageable, Principal principal, String type, String keyword) {
        //String username = principal.getName(); // 로그인한 사용자 정보
        Pageable temp = PageRequest.of(pageable.getPageNumber(), 10, Sort.Direction.DESC, "qaNum");
        
        Page<Qa> qaList;

        // 검색 조건이 있을 경우 필터링
        if (keyword != null && !keyword.isEmpty()) {
            if ("S".equals(type)) {
                qaList = qaRepository.findByQaTitleContaining(keyword, temp);
            } else if ("C".equals(type)) {
                qaList = qaRepository.findByQaContentContaining(keyword, temp);
//            } else if ("A".equals(type)) {
//                qaList = qaRepository.findByQaWriterContaining(keyword, temp);
            } else if ("SC".equals(type)) {
                qaList = qaRepository.findByQaTitleContainingOrQaContentContaining(keyword, keyword, temp);
//            } else if ("SCA".equals(type)) {
//                qaList = qaRepository.findByQaWriterContainingOrQaTitleContainingOrQaContentContaining(keyword, keyword, keyword, temp);
            } else {
                qaList = qaRepository.findAll(temp); // 검색 조건이 없을 경우 전체 조회
            }
        } else {
            qaList = qaRepository.findAll(temp); // 검색 없이 전체 조회
        }
        Page<QaDTO> QaDTO = qaList.map(qa -> modelMapper.map(qa, QaDTO.class));

        return QaDTO;
    }
    //읽기
    @Override
    public QaDTO qaRead(Long qaNum) {
        Qa qa = qaRepository.findById(qaNum).orElseThrow();
        QaDTO qaDTO = modelMapper.map(qa, QaDTO.class);
        return qaDTO;
    }
    @Override
    public void qaAnswered(Long qaNum) {
        Qa qa = qaRepository.findById(qaNum).orElseThrow();
        qa.setQaAnswered(true); // 또는 "답변 완료"
        qaRepository.save(qa);
    }
    //삭제
    @Override
    public void qaDelete(Long qaNum) {
        qaRepository.deleteById(qaNum);
    }
}
