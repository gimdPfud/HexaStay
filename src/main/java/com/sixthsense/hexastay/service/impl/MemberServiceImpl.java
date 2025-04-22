package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    //member 레퍼스토리 가져오기
    private final MemberRepository memberRepository;

    //ModelMapper
    private final ModelMapper modelMapper = new ModelMapper();


    //등록
    //todo : 기본 등록 메서드만 작업 했음  - 참조 받을실분은 참조 pk 수정해서 쓰세요
    @Override
    public void memberinsert(MemberDTO memberDTO) {

        //변환 - Memem만 DTO 타입으로 변환
        Member member = modelMapper.map(memberDTO, Member.class);

        //처리
        memberRepository.save(member);

    }

    //리스트 목록
    @Override
    public Page<MemberDTO> memberList( Pageable page) {

        //********페이지 처리 ************//
        //시작 페이지 설정
        int firstPage = page.getPageNumber() - 1;

        //총 토탈 페이지 설정 - 토탈 페이지는 갯수는 여기서 설정 가능
        int pageLimites = 5;

        //페이지 재정의후 페이지 조립
        Pageable pageable =
                PageRequest.of(firstPage, pageLimites, Sort.by(Sort.Direction.DESC,"memberNum"));

        //*** 변환 및 처리 작업 **//
        //엔티티 변수 선언
        Page<Member> memberEntity;

        //memberEntity 페이지 처리
        memberEntity =
                memberRepository.findAll(pageable);

        //todo : memberRepository에서 검색설정후 검색 메서드 구현 예정


        //변환    - 람다식으로 변환
        Page<MemberDTO> memberDTOS =
                memberEntity.map(data -> modelMapper.map(data, MemberDTO.class));


        //최종 반홥 타입
        return memberDTOS;
    }

    //읽기
    @Override
    public MemberDTO memberRead(Long memberNum) {
        //todo : 예외 처리는 추후에 할 예정 입니다.

        //Entity 가져오기
        Optional<Member> optionalMember =
                memberRepository.findById(memberNum);

        //변환 Entity >> DTO
        MemberDTO memberDTO = modelMapper.map(optionalMember, MemberDTO.class);

        //최종 반환 타입
        return memberDTO;
    }

    //수정
    @Override
    public MemberDTO memberModify(MemberDTO memberDTO) {
        // memberDTO -> Member 엔티티로 변환
        Member member = memberRepository.findById(memberDTO.getMemberNum())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setMemberName(memberDTO.getMemberName());
        member.setMemberPhone(memberDTO.getMemberPhone());
        member.setMemberEmail(memberDTO.getMemberEmail());

        // 수정된 회원 엔티티를 다시 저장
        memberRepository.save(member);

        // 수정된 회원 정보를 DTO로 변환하여 반환
        return new MemberDTO(member);
    }

    //삭제
    @Override
    public void memberDelet(Long memberNum) {

        //삭제처리 - member테이블에 있는 pk를 가져와서 행 삭제 처리
        memberRepository.deleteById(memberNum);


    }


    //todo:http://localhost:8090/register-hotelroom room 배정 member검색 로직
    public List<MemberDTO> searchByNameOrEmail(String keyword) {
        List<Member> members = memberRepository.searchByNameOrEmail(keyword);
        return members.stream()
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());
    }
}
