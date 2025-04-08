package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.dto.FaqDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Faq;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.FaqRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.FaqService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository; // (유지)
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final AdminRepository adminRepository;

    //등록
    @Override
    public void faqInsert(FaqDTO faqDTO, Principal principal) {
        //String email = principal.getName();
        // (1) 여기서 멤버 조회 및 예외 처리
        Member member = memberRepository.findById(faqDTO.getMemberNum()).orElseThrow(EntityNotFoundException::new);
//        if (!member.getRole().equals("ADMIN")) {
//            throw new RuntimeException("관리자만 FAQ를 등록할 수 있습니다.");
//        }
        log.info("확인완료" + member);
        // (2) DTO → Entity 변환
        Faq faq = modelMapper.map(faqDTO, Faq.class); // (수정: ModelMapper 사용)
        faq.setMember(member);
        // (3) 저장 및 로깅
        log.info("faq 저장 전: {}", faq.toString());
        faqRepository.save(faq);
        log.info("faq 저장 완료. 작성자 ID: {}", member.getMemberNum());
    }

    //목록
    public List<FaqDTO> faqList(String category) {
        log.info("faqList() 실행 - 카테고리: {}", category);
        List<Faq> faqList = faqRepository.findByFaqCategory(category);
        return faqList.stream()
                .map(faq -> modelMapper.map(faq, FaqDTO.class))
                .collect(Collectors.toList());
    }

    // 단건 조회
    @Override
    public FaqDTO faqRead(Long faqNum) {
        log.info("보기");
        Faq faq = faqRepository.findById(faqNum)
                .orElseThrow(() -> new RuntimeException("faqNum=" + "FaqDTO()" + "에 해당하는 FAQ가 없습니다."));
        return modelMapper.map(faq, FaqDTO.class); // (수정: ModelMapper 사용)
    }

    // 수정
    @Override
    public void faqModify(FaqDTO faqDTO) {
        log.info("수정 진입");
        Faq faq = faqRepository.findById(faqDTO.getFaqNum())
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        //ModelMapper를 사용하여 기존 Entity에 DTO 값 주입
        modelMapper.map(faqDTO, faq);
        log.info("수정 맵핑");
        faqRepository.save(faq);
        log.info("수정완료");
    }

    // 삭제
    @Override
    public void faqDelete(Long faqNum) {
//        if (!faqRepository.existsById(faqNum)) {
//            throw new RuntimeException("FAQ를 찾을 수 없습니다.");
//        }
        faqRepository.deleteById(faqNum);
        log.info("삭제완료");
    }
    @Override
    public String AdminRole(Principal principal) {
        String adminEmail = principal.getName();
        Admin admin = adminRepository.findByAdminEmail(adminEmail); // 변수 오타 수정
        return admin.getAdminRole(); // ex: admin, wait 등
    }
}