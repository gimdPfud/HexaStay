package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.MemberDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    //등록
    //todo기본 로직 만 만들었음 - 참조 pk 가져 오실분은 등록만 수정 보시면 됩니다.
    public void memberinsert(MemberDTO memberDTO);

    //리스트
    public Page<MemberDTO> memberList(Pageable page);

    //3-1.읽기
    public MemberDTO memberRead(Long memberNum);


    //수정
    public MemberDTO memberModify(MemberDTO memberDTO);

    //삭제
    public void memberDelet(Long memberNum);


    //todo:http://localhost:8090/register-hotelroom room 배정 member검색 로직
    public List<MemberDTO> searchByNameOrEmail(String query);



}
