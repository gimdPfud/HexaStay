package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.NoticeDTO;
import com.sixthsense.hexastay.entity.Notice;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.NoticeRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.NoticeService;
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
    //등록
    @Override
    public void noticeInsert(NoticeDTO noticeDTO){
        noticeDTO.setNoticeWriter("세영바보");
        noticeDTO.setMemberNum(1l);

        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        Member member =memberRepository.findById(noticeDTO.getMemberNum()).orElseThrow();
        notice.setNoticeView(1);
        notice.setMember(member);
        log.info(notice.toString());
        notice = noticeRepository.save(notice);
        log.info("보드"+ noticeDTO.getMemberNum());
        noticeRepository.save(notice);
    }
    //목록
    @Override
    public Page<NoticeDTO> noticeList(Pageable pageable, Principal principal, String type, String keyword) {
        //String username = principal.getName(); // 로그인한 사용자 정보
        int currentPage = pageable.getPageNumber() - 1;
        int limits = 10;
        Pageable temp = PageRequest.of(currentPage, limits, Sort.Direction.DESC, "noticeNum");

        Page<Notice> noticeList;

        // 검색 조건이 있을 경우 필터링
        if (keyword != null && !keyword.isEmpty()) {
            if ("S".equals(type)) {
                noticeList = noticeRepository.findByNoticeTitleContaining(keyword, temp);
            } else if ("C".equals(type)) {
                noticeList = noticeRepository.findByNoticeContentContaining(keyword, temp);
            } else if ("A".equals(type)) {
                noticeList = noticeRepository.findByNoticeWriterContaining(keyword, temp);
            } else if ("SC".equals(type)) {
                noticeList = noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, temp);
            } else if ("SCA".equals(type)) {
                noticeList = noticeRepository.findByNoticeWriterContainingOrNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, keyword, temp);
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
        Optional<Notice> temp = noticeRepository.findById(noticeDTO.getNoticeNum());
        //변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        //SQL처리
        noticeRepository.save(notice);
    }

    //삭제
    @Override
    public void noticeDelete(Long noticeNum) {

    }





}
