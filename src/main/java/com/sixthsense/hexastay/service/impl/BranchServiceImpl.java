package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.repository.BranchRepository;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.service.BranchService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CenterRepository centerRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void branchInsert(BranchDTO branchDTO) {
        log.info("branch Insert Service 진입");
        //branch 등록
        Branch branch = modelMapper.map(branchDTO, Branch.class);
        branchRepository.save(branch);

        log.info("branchDTO를 Entity로 변환 완료 : " + branch);
    }

    @Override
    public Page<BranchDTO> branchList(Pageable pageable) {
        log.info("branch List Service 진입");

        //branch 목록
        Page<Branch> branchPage = branchRepository.findAll(pageable);
        Page<BranchDTO> branchDTOS = branchPage.map(branch -> modelMapper.map(branch, BranchDTO.class));

        return branchDTOS;
    }

    @Override
    public BranchDTO branchRead(Long branchNum) {
        log.info("branch Read Service 진입");

        //branch 상세보기
        Branch branch = branchRepository.findById(branchNum).get();
        BranchDTO branchDTO = modelMapper.map(branch, BranchDTO.class);

        return branchDTO;
    }

    @Override
    public void branchModify(BranchDTO branchDTO) {
        log.info("branch Modify Service 진입");

        //branch 수정
        Branch branch = branchRepository.findById(branchDTO.getBranchNum()).orElseThrow();

        branch.setBranchNum(branchDTO.getBranchNum());
        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchPhone(branchDTO.getBranchPhone());
        branch.setBranchEmail(branchDTO.getBranchEmail());
        branch.setBranchAddress(branchDTO.getBranchAddress());
        branch.setBranchCeoName(branchDTO.getBranchCeoName());
        branch.setBranchBusinessNum(branchDTO.getBranchBusinessNum());

        branchRepository.save(branch);

        log.info("수정 반영된 내용 : " + branch);
    }

    @Override
    public void branchDelete(Long branchNum) {

        branchRepository.deleteById(branchNum);

        log.info("삭제된 pk : " + branchNum);
    }

    @Override
    public List<BranchDTO> allBranchList() {
        List<Branch> branchList = branchRepository.findAll();
        List<BranchDTO> branchDTOList = new ArrayList<>();
        for(Branch branch : branchList) {
            BranchDTO branchDTO = modelMapper.map(branch, BranchDTO.class);
            branchDTOList.add(branchDTO);
        }
        return branchDTOList;
    }



}
