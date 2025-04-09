package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void branchInsert(BranchDTO branchDTO) throws IOException {
        log.info("branch Insert Service 진입");

        if (branchDTO.getCompanyPicture() != null && !branchDTO.getCompanyPicture().isEmpty()) {
            String fileOriginalName = branchDTO.getCompanyPicture().getOriginalFilename();
            String fileFirstName = branchDTO.getBranchNum() + "_" + branchDTO.getBranchName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            branchDTO.setCompanyPictureMeta("/branch/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "branch/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "branch/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            branchDTO.getCompanyPicture().transferTo(uploadPath.toFile());
        }

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
    public BranchDTO branchRead(Long id) {
        log.info("branch Read Service 진입");

        //branch 상세보기
        Branch branch = branchRepository.findById(id).get();
        BranchDTO branchDTO = modelMapper.map(branch, BranchDTO.class);

        return branchDTO;
    }

    @Override
    public void branchModify(BranchDTO branchDTO) {
        log.info("branch Modify Service 진입");

        //branch 수정(지사 Entity 조회)
        Branch branch = branchRepository.findById(branchDTO.getBranchNum()).orElseThrow();
        log.info("수정할 branchNum : " + branchDTO.getBranchNum());

        //center Entity 조회
        log.info("수정할 centerNum : " + branchDTO.getCenterNum());
        Center center = centerRepository.findById(branchDTO.getCenterNum()).orElseThrow();

        //지사 정보 업데이트
        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchPhone(branchDTO.getBranchPhone());
        branch.setBranchEmail(branchDTO.getBranchEmail());
        branch.setBranchAddress(branchDTO.getBranchAddress());
        branch.setBranchCeoName(branchDTO.getBranchCeoName());
        branch.setBranchBusinessNum(branchDTO.getBranchBusinessNum());

        //본사 정보 업데이트
        branch.setCenter(center);

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

    //조직명 검색
    @Override
    public Page<BranchDTO> companyName(String keyword, Pageable pageable) {
        Page<Branch> branchList = branchRepository.findByBranchNameContaining(keyword, pageable);
        Page<BranchDTO> branchDTOList = branchList.map(branch -> modelMapper.map(branch, BranchDTO.class));

        return branchDTOList;
    }

    // 브랜드명 검색
    @Override
    public Page<BranchDTO> brandName(String keyword, Pageable pageable){
        Center center = centerRepository.findByCenterBrand(keyword);
        Long centerNum = center.getCenterNum();
        Page<Branch> branchList = branchRepository.findByCenter_CenterNum(centerNum, pageable);
        Page<BranchDTO> brachDTOList = branchList.map(branch -> modelMapper.map(branch, BranchDTO.class));
        return brachDTOList;
    }

    @Override
    public Page<BranchDTO> branchBusinessNum(String keyword, Pageable pageable) {
        //검색하면 사업자등록번호를 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Branch> branchPage = branchRepository.findByBranchNameContaining(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<BranchDTO> brachDTOList = branchPage.map(branch -> modelMapper.map(branch, BranchDTO.class));
        //Page타입 DTO를 반환
        return brachDTOList;
    }


}
