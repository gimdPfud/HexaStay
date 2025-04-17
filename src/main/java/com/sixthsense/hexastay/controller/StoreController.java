/***********************************************
 * 클래스명 : StoreController
 * 기능 : 외부업체를 등록 및 기타 기능을 사용하는 페이지와 맵핑
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/store")
public class StoreController {
    private final StoreService storeService;
    private final CompanyService companyService;
    private final ModelMapper modelMapper = new ModelMapper();//todo 임시조치 끝나면 지우기
    private final CompanyRepository companyRepository;
    private final AdminService adminService; //adminRepository에는 email로 찾아오는게 있는데.. 여긴 없음.

    /*
     * 메소드명 :
     * 인수 값 :
     * 리턴 값 :
     * 기  능 :
     * */
    @GetMapping("/insert") // TODO: 호텔 GM만 접근 가능한 페이지
    public String insert(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
//        log.info(adminDTO);

//        // 소속된 회사 없음
//        if (adminDTO.getCompanyNum() == null) {
//            if (adminDTO.getStoreNum() != null) {
//                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
//                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
//            } else {
//                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
//                return "redirect:/admin/logout";
//            }
//        }
        //todo 임시조치
        List<CompanyDTO> list = new ArrayList<>();
        List<CompanyDTO> fcL = companyRepository.findByCompanyType("facility").stream().map(data->modelMapper.map(data,CompanyDTO.class)).toList();
        List<CompanyDTO> brL = companyRepository.findByCompanyType("branch").stream().map(data->modelMapper.map(data,CompanyDTO.class)).toList();
        list.addAll(brL);
        list.addAll(fcL);
        model.addAttribute("companyList",list);
//         정상적인 호텔 소속 어드민
//        CompanyDTO companyDTO = companyService.companyRead(adminDTO.getCompanyNum());
//        model.addAttribute("data", companyDTO);
        return "store/insert";
    }

    @PostMapping("/insert")
    public String insert(StoreDTO storeDTO) throws IOException {
//        log.info("등록post : "+storeDTO);
        storeService.insert(storeDTO);
        return "redirect:/admin/store/list";
    }


    @GetMapping("/list")
    public String list(Pageable pageable, Model model,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) Long companyNum,
                       @RequestParam(required = false) String keyword){
        Page<StoreDTO> list = storeService.searchlist(companyNum, searchType, keyword, pageable);
        model.addAttribute("list",list);
        /*친구찬스*/
        Map<Long, String> uniqueCompanies = list.stream()
                .collect(Collectors.toMap(
                        StoreDTO::getCompanyNum,
                        StoreDTO::getCompanyName,
                        (existing, replacement) -> existing, // 중복 키 무시
                        LinkedHashMap::new
                ));
        model.addAttribute("companyMap", uniqueCompanies);
        /*친구찬스끝*/
        model.addAttribute("searchType",searchType);
        model.addAttribute("chosenCompany",companyNum);
        model.addAttribute("keyword",keyword);
        return "store/list";
    }
    @PostMapping("/list")/*todo superAdmin만 접근 가능한 페이지*/
    public String list(RedirectAttributes model, Principal principal, Pageable pageable,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) String chosenCompany,
                       @RequestParam(required = false) String keyword){
//        log.info("searchType : " + searchType);
//        log.info("chosenCompany : " + chosenCompany);
//        log.info("keyword : " + keyword);
//        log.info("pageable : " + pageable.toString());
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        // 소속된 회사 없음
//        if (adminDTO.getCompanyNum() == null) {
//            if (adminDTO.getStoreNum() != null) {
//                log.info("어드민이지만 회사 소속 아님. 대신 스토어 소속: {}", adminDTO.getStoreNum());
//                return "redirect:/admin/store/read?idid=" + adminDTO.getStoreNum();
//            } else {
//                log.warn("회사도 스토어도 소속 안 된 어드민 접근: {}", adminDTO.getAdminEmail());
//                return "redirect:/admin/logout";
//            }
//        }
        Long companyNum = 0L;
        if(chosenCompany!=null && !chosenCompany.trim().isEmpty() && !chosenCompany.equals("호텔목록")){
            companyNum = Long.valueOf(chosenCompany);
        }
//        log.info(companyNum);
//        Page<StoreDTO> list = storeService.searchlist(companyNum, searchType, keyword, pageable);
//        list.forEach(log::info);
        model.addAttribute("companyNum",companyNum);
//        /*친구찬스*/
//        Map<Long, String> uniqueCompanies = list.stream()
//                .collect(Collectors.toMap(
//                        StoreDTO::getCompanyNum,
//                        StoreDTO::getCompanyName,
//                        (existing, replacement) -> existing, // 중복 키 무시
//                        LinkedHashMap::new
//                ));
//        model.addAttribute("companyMap", uniqueCompanies);
//        /*친구찬스끝*/
        model.addAttribute("searchType",searchType);
        model.addAttribute("keyword",keyword);
        return "redirect:/admin/store/list";
    }


    @GetMapping("/read")
    public String read(Long idid, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO admin = adminService.adminFindEmail(principal.getName());
        if (idid == null) {
            if(admin.getStoreNum()==null){
                log.info("스토어소속이 아닌데 /admin/store/read로 접근. /list로 반환한다.");
                return "redirect:/admin/store/list";
            }else{
                idid = admin.getStoreNum();
            }
        }
        StoreDTO data = storeService.read(idid);
        boolean result = storeService.validStoreAdmin(admin, data);
        if (result) {
            model.addAttribute("data", data);
            return "store/read";
        } else {
            log.info("다른 가게의 상세정보를 보려고 함.");
            return "redirect:/admin/logout";
        }
    }


    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id,Principal principal, Model model){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        StoreDTO data = storeService.read(id);
        storeService.validStoreAdmin(adminDTO,data);
        model.addAttribute("data",data);
        return "store/modify";
    }
    @PostMapping("/modify")
    public String modify(StoreDTO storeDTO) throws IOException {
        Long storeNum = storeService.modify(storeDTO);
        return "redirect:/admin/store/read?idid="+storeNum;
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        storeService.delete(id);
        return "redirect:/admin/store/list";
    }
}
