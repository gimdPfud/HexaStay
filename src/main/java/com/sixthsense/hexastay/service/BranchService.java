package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BranchService {

    //branch 등록
    public void branchInsert(BranchDTO branchDTO) throws IOException;

    //branch 목록
    public Page<BranchDTO> branchList(Pageable pageable);

    //branch 상세보기
    public BranchDTO branchRead(Long branchNum);

    //branch 수정
    public void branchModify(BranchDTO branchDTO);

    //branch 삭제
    public void branchDelete(Long branchNum);

    //조회용
    public List<BranchDTO> allBranchList();

    //조직명으로 조회
    public Page<BranchDTO> companyName(String keyword, Pageable pageable);

    //브랜드로 조회
    public Page<BranchDTO> brandName(String keyword, Pageable pageable);

    //지사 사업자등록번호로 조회
    public Page<BranchDTO> branchBusinessNum(String keyword, Pageable pageable);
}
