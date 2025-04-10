package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.companyDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Center;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CenterRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.service.CenterService;
import com.sixthsense.hexastay.service.CompanyService;
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
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public void centerInsert(CompanyDTO companyDTO) throws IOException {
        log.info("center Insert Service 진입");

        if (companyDTO.getCompanyPicture() != null && !companyDTO.getCompanyPicture().isEmpty()) {
            String fileOriginalName = companyDTO.getCompanyPicture().getOriginalFilename();
            String fileFirstName = companyDTO.getCenterNum() + "_" + companyDTO.getCenterName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            companyDTO.setCompanyPictureMeta("/center/" + fileName);
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "center/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "center/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            companyDTO.getCompanyPicture().transferTo(uploadPath.toFile());

        }


        //center 등록 (DTO를 Entity로 변환해서 Entity에 담고)
        Center center = modelMapper.map(companyDTO, Center.class);
        //Entity에 저장
        center = centerRepository.save(center);

        log.info("companyDTO를 Entity로 변환 완료 : " + center);
    }



    @Override
    public Page<companyDTO> centerList(Pageable pageable) {
        log.info("center List Service 진입");

        //center 목록 (findAll로 전체 목록 불러와서 페이징 후 Entity에 저장)
        Page<Center> centerPage = centerRepository.findAll(pageable);
        //Entity를 DTO로 변환 후 저장(페이징 된 상태로)
        Page<companyDTO> companyDTOS = centerPage.map(center -> modelMapper.map(center, companyDTO.class));
        //DTO를 반환
        return companyDTOS;
    }

    @Override
    public companyDTO centerRead(Long id) {
        log.info("center Read Service 진입");

        //center 상세보기 (pk로 Entity 찾기)
        Center center = centerRepository.findById(id).orElse(null);

        if(center == null){
            return null;
        }

        //Entity를 DTO로 변환 후 DTO 반환
        companyDTO companyDTO = modelMapper.map(center, companyDTO.class);
        //DTO를 반환
        return companyDTO;
    }


    @Override
    public void centerModify(companyDTO companyDTO) {
        log.info("center Modify Service 진입");

        //center 수정(DTO에서 pk로 Entity 찾기)
        Center center = centerRepository.findById(companyDTO.getCenterNum()).orElseThrow();

        //수정 내용 Entity에 set 해주기
        center.setCenterBrand(companyDTO.getCenterBrand());
        center.setCenterName(companyDTO.getCenterName());
        center.setCenterPhone(companyDTO.getCenterPhone());
        center.setCenterEmail(companyDTO.getCenterEmail());
        center.setCenterAddress(companyDTO.getCenterAddress());
        center.setCenterCeoName(companyDTO.getCenterCeoName());
        center.setCenterBusinessNum(companyDTO.getCenterBusinessNum());

        //Entity에 저장
        centerRepository.save(center);

        log.info("수정 반영된 내용 : " + center);
    }

    @Override
    public void centerDelete(Long centerNum) {
        log.info("center Delete Service 진입");

        //참조하고있는 admin 먼저 삭제 (pk로 Entity 찾아서 삭제)
        adminRepository.deleteByCenter_CenterNum(centerNum);

        //참조하고있는 branch 먼저 삭제 (pk로 Entity 찾아서 삭제)
        branchRepository.deleteByCenter_CenterNum(centerNum);

        //참조하고있는 facility 먼저 삭제 (pk로 Entity 찾아서 삭제)
        facilityRepository.deleteByCenter_CenterNum(centerNum);

        //center 삭제 (pk로 Entity 찾아서 삭제)
        centerRepository.deleteById(centerNum);

        log.info("삭제된 pk : " + centerNum);
    }

    //가입용
    @Override
    public List<companyDTO> allCenterList(){
        List<Center> centerList = centerRepository.findAll();
        List<companyDTO> companyDTOList = new ArrayList<>();
        for (Center center : centerList) {
            companyDTO companyDTO = modelMapper.map(center, companyDTO.class);
            companyDTOList.add(companyDTO);
        }
        return companyDTOList;
    }

    // 본사명 조회용
    @Override
    public Page<companyDTO> companyName(String keyword, Pageable pageable){
        //검색하면 본사명을 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerList = centerRepository.findByCenterNameContaining(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<companyDTO> companyDTOList = centerList.map(center -> modelMapper.map(center, companyDTO.class));
        //Page타입 DTO를 반환
        return companyDTOList;
    }

    // 브랜드명 조회용
    @Override
    public Page<companyDTO> brandName(String keyword, Pageable pageable){
        //검색하면 브랜드명을 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerPageList = centerRepository.findByCenterBrand(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<companyDTO> companyDTOList = centerPageList.map(center -> modelMapper.map(center, companyDTO.class));
        //Page타입 DTO를 반환
        return companyDTOList;
    }

    //사업자등록번호 조회용
    @Override
    public Page<companyDTO> centerBusinessNum(String keyword, Pageable pageable) {
        //검색하면 사업자등록번호를 포함하는 정보를 Page타입 Entity로 불러와서 담기
        Page<Center> centerPageList = centerRepository.findByCenterBusinessNum(keyword, pageable);
        //Entity를 DTO로 변환하여 Page타입 DTO로 불러와서 담기
        Page<companyDTO> companyDTOList = centerPageList.map(center -> modelMapper.map(center, companyDTO.class));
        //Page타입 DTO를 반환
        return companyDTOList;
    }

    @Override
    public List<String> findDistinctCenterBrand() {

        return centerRepository.findDistinctCenterBrand();
    }

    @Override
    public List<String> findDistinctCenterName() {

        return centerRepository.findDistinctCenterName();
    }


}
