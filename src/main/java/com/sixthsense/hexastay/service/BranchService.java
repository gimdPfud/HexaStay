package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BranchService {

    //branch 등록
    public void branchInsert(BranchDTO branchDTO);

    //branch 목록
    public Page<BranchDTO> branchList(Pageable pageable);

    //branch 상세보기
    public BranchDTO branchRead(Long branchNum);

    //branch 수정
    public void branchModify(BranchDTO branchDTO);

    //branch 삭제
    public void branchDelete(Long branchNum);


}
