package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.NoticeDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Notice;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.NoticeRepository;
import com.sixthsense.hexastay.service.NoticeService;
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
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void noticeInsert(NoticeDTO noticeDTO) {
        // 임시 작성자 지정 (하드코딩된 값)
//        noticeDTO.setNoticeWriter("spit착맨"); // TODO: 로그인 사용자로 대체 예정
//        noticeDTO.setMemberNum(1L);           // TODO: 실제 로그인한 멤버 ID로 대체 예정

        // (1) 여기서 멤버 조회 및 예외 처리
        Member memberOpt = memberRepository.findById(noticeDTO.getMemberNum()).orElseThrow(EntityNotFoundException::new);

        // (2) DTO → Entity 변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        notice.setNoticeView(1);
        notice.setMember(memberOpt); // (중요)

        // (3) 저장 및 로깅
        log.info("Notice 저장 전: {}", notice.toString());
        noticeRepository.save(notice); // 중복 저장 제거
        log.info("Notice 저장 완료. 작성자 ID: {}", memberOpt.getMemberNum()               );
    }
    //목록
    @Override
    public Page<NoticeDTO> noticeList(Pageable pageable, Principal principal, String type, String keyword) {
        //String username = principal.getName(); // 로그인한 사용자 정보
        Pageable temp = PageRequest.of(pageable.getPageNumber(), 10, Sort.Direction.DESC, "noticeNum");
        
        Page<Notice> noticeList;

        // 검색 조건이 있을 경우 필터링
        if (keyword != null && !keyword.isEmpty()) {
            if ("S".equals(type)) {
                noticeList = noticeRepository.findByNoticeTitleContaining(keyword, temp);
            } else if ("C".equals(type)) {
                noticeList = noticeRepository.findByNoticeContentContaining(keyword, temp);
//            } else if ("A".equals(type)) {
//                noticeList = noticeRepository.findByNoticeWriterContaining(keyword, temp);
            } else if ("SC".equals(type)) {
                noticeList = noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, temp);
//            } else if ("SCA".equals(type)) {
//                noticeList = noticeRepository.findByNoticeWriterContainingOrNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, keyword, temp);
            } else {
                noticeList = noticeRepository.findAll(temp); // 검색 조건이 없을 경우 전체 조회
            }
        } else {
            noticeList = noticeRepository.findAll(temp); // 검색 없이 전체 조회
        }
        Page<NoticeDTO> NoticeDTO = noticeList.map(notice -> modelMapper.map(notice, NoticeDTO.class));

        return NoticeDTO;
    }

    //읽기
    @Override
    public NoticeDTO noticeRead(Long noticeNum) {
        Notice notice = noticeRepository.findById(noticeNum).orElseThrow();
        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);
        return noticeDTO;
    }
    //수정
    @Override
    public void noticeModify(NoticeDTO noticeDTO) {
        //기존 데이터를 조회를해서
        Optional<Notice> search = noticeRepository.findById(noticeDTO.getNoticeNum());
        //변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        //SQL 처리
        noticeRepository.save(notice);
    }

    //삭제
    @Override
    public void noticeDelete(Long noticeNum) {
        noticeRepository.deleteById(noticeNum);
    }
}
