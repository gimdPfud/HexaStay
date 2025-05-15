/***********************************************
 * 클래스명 : StoreServiceImpl
 * 기능 : 외부업체 서비스 구현객체
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.*;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.StoreService;
import com.sixthsense.hexastay.service.ZzService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class StoreServiceImpl implements StoreService {
    //    private final RoomRepository roomRepository;
//    private final HotelRoomRepository hotelRoomRepository;
//    private final MemberRepository memberRepository;
//    private final CompanyService companyService;
    private final ZzService zzService;
    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoreTranslationRepository storeTranslationRepository;

    /* 헬퍼 메소드 */

    private boolean isValidTranslationString(String value) {
        return value != null && !value.isEmpty();
    }

    private StoreDTO convertToStoreDTOWithTranslation(Store storeEntity, Locale locale) {
        if (storeEntity == null) {
            log.warn("convertToStoreDTOWithTranslation: storeEntity is null.");
            return null; // 또는 예외를 던지거나, 빈 StoreDTO 반환
        }
        log.debug("Converting Store ID: {} to StoreDTO for locale: {}", storeEntity.getStoreNum(), locale.toLanguageTag());

        // 1. Store 엔티티의 기본 정보(한국어)를 StoreDTO로 매핑
        StoreDTO storeDTO = modelMapper.map(storeEntity, StoreDTO.class);
        // modelMapper가 companyName을 매핑하지 않는다면 수동 설정
        if (storeEntity.getCompany() != null) {
            storeDTO.setCompanyName(storeEntity.getCompany().getCompanyName());
        }


        String targetLocale = locale.getLanguage();

        // 2. 기본 언어(한국어)가 아닌 경우 번역 적용 시도
        if (!"ko".equals(targetLocale) && targetLocale != null && !targetLocale.isEmpty()) {
            log.debug("Attempting translation for Store ID: {}, locale: {}", storeEntity.getStoreNum(), targetLocale);
            try {
                Optional<StoreTranslation> translationOpt = storeTranslationRepository
                        .findByStore_StoreNumAndLocale(storeEntity.getStoreNum(), targetLocale);

                if (translationOpt.isPresent()) {
                    StoreTranslation translation = translationOpt.get();
                    log.info("Translation FOUND for Store ID {}: Name='{}', Category='{}'",
                            storeEntity.getStoreNum(),
                            translation.getStoreName(),
                            translation.getStoreCategory());

                    // 번역된 값으로 StoreDTO 필드 덮어쓰기
                    if (isValidTranslationString(translation.getStoreName())) {
                        storeDTO.setStoreName(translation.getStoreName());
                    }
                    if (isValidTranslationString(translation.getStoreCategory())) {
                        storeDTO.setStoreCategory(translation.getStoreCategory());
                    }
                    // 주소 등 다른 필드도 번역한다면 여기에 추가
                    // if (isValidTranslationString(translation.getStoreAddress())) {
                    //     storeDTO.setStoreAddress(translation.getStoreAddress());
                    // }
                } else {
                    log.warn("No translation found for Store ID: {} and locale: {}. Using default (Korean) values.",
                            storeEntity.getStoreNum(), targetLocale);
                    // 기본값(한국어)은 이미 modelMapper.map을 통해 storeDTO에 설정되어 있음
                }
            } catch (Exception e) {
                log.error("Error applying translation for Store ID: {}. Using default (Korean) values.",
                        storeEntity.getStoreNum(), e);
                // 예외 발생 시에도 DTO는 한국어 기본값을 유지
            }
        } else {
            log.debug("Locale is Korean or invalid for Store ID: {}. Using default (Korean) values.", storeEntity.getStoreNum());
        }
        return storeDTO;
    }

    /*
     * 메소드명 : insert
     * 인수 값 : StoreDTO
     * 리턴 값 : Long
     * 기  능 : Store 등록 후 등록한 객체의 pk 반환
     * */
    @Override
    public Long insert(StoreDTO storeDTO) throws IOException {
        //들어온 DTO -> Entity 변환
        Store store = modelMapper.map(storeDTO, Store.class);
        //일단 저장 해서 pk 생성. (저장 안하면 pk 없으니까)
        store = storeRepository.save(store);

        //들어온 DTO에 사진에 대한 정보가 있다면
        if(storeDTO.getStoreProfile()!=null&& !storeDTO.getStoreProfile().isEmpty()){
            //저장할 때 필요한 데이터들을 설정한다.
            //  1. 파일 이름 가져옴
            String fileOriginalName = storeDTO.getStoreProfile().getOriginalFilename();
            //  2. 상호명_저장된pk
            String fileFirstName = storeDTO.getStoreName() + "_" + store.getStoreNum();
            //  3. 확장자 (온점 포함)
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            //  4. 파일이름 = 상호명_저장된pk.확장자
            String fileName = fileFirstName + fileSubName;

            //파일은  /store/상호명_저장된pk.확장자  임.
            storeDTO.setStoreProfileMeta("/store/"+fileName);

            //지금까지 만든 경로로 파일을 저장한다. (저장할 폴더가 없다면 생성)
            Path uploadPath = Paths.get("c:/data/hexastay","store/"+fileName);
            Path createPath = Paths.get("c:/data/hexastay","store/");
            if(!Files.exists(createPath)){
                Files.createDirectory(createPath);
            }
            storeDTO.getStoreProfile().transferTo(uploadPath.toFile());
        }
        //파일의 데이터(/store/상호명_저장된pk.확장자)를 저장한다.
        store.setStoreProfileMeta(storeDTO.getStoreProfileMeta());
        //다시 저장 (이때, 이미 pk를 가지고 있으므로 update쿼리가 나간다.)
        store = storeRepository.save(store);
        return store.getStoreNum();
    }


    /*
     * 메소드명 : read
     * 인수 값 : Long pk
     * 리턴 값 : StoreDTO
     * 기  능 : pk값으로 StoreDTO 단일 객체를 찾아옴
     * */
    @Override
    public StoreDTO read(Long pk, Locale locale) { //
        log.info("스토어 상세정보 다국어 서비스 진입 - 스토어 ID: {}, 요청 locale: {}", pk, locale.getLanguage());

        Store store = storeRepository.findById(pk)
                .orElseThrow(() -> {
                    log.error("스토어를 찾을 수 없습니다. ID: {}", pk);
                    return new EntityNotFoundException("요청하신 스토어 정보를 찾을 수 없습니다. ID: " + pk);
                });

        // 1. Store 엔티티의 기본 정보(한국어)를 StoreDTO로 매핑
        StoreDTO storeDTO = modelMapper.map(store, StoreDTO.class);

        String targetLocale = locale.getLanguage();

        // 2. 기본 언어(한국어)가 아닌 경우 번역 적용 시도
        if (!"ko".equals(targetLocale) && targetLocale != null && !targetLocale.isEmpty()) {
            Optional<StoreTranslation> translationOpt =
                    storeTranslationRepository.findByStore_StoreNumAndLocale(pk, targetLocale);

            if (translationOpt.isPresent()) {
                StoreTranslation translation = translationOpt.get();
                log.info("스토어 ID {}에 대한 '{}' 번역 찾음: {}", pk, targetLocale, translation.getStoreName());

                // 번역된 값으로 StoreDTO 필드 덮어쓰기
                if (translation.getStoreName() != null && !translation.getStoreName().isEmpty()) {
                    storeDTO.setStoreName(translation.getStoreName());
                }
                if (translation.getStoreCategory() != null && !translation.getStoreCategory().isEmpty()) {
                    storeDTO.setStoreCategory(translation.getStoreCategory());
                }

                 if (translation.getStoreAddress() != null && !translation.getStoreAddress().isEmpty()) {
                     storeDTO.setStoreAddress(translation.getStoreAddress());
                 }

            } else {
                log.warn("스토어 ID {}에 대한 '{}' 번역 정보 없음. 기본값(한국어) 사용.", pk, targetLocale);

            }
        } else {
            log.info("스토어 ID {}에 대한 한국어 값 사용 (또는 locale 정보 없음).", pk);
        }

        return storeDTO;
    }


    /*
     * 메소드명 : modify
     * 인수 값 : StoreDTO
     * 리턴 값 : Long
     * 기  능 : storeDTO를 받아서 수정 한 후 수정한 객체의 pk값을 반환한다.
     * */
    @Override
    public Long modify(StoreDTO storeDTO) throws IOException {
        Store store = storeRepository.findById(storeDTO.getStoreNum()).orElseThrow(EntityNotFoundException::new);
        if(storeDTO.getStoreProfile()!=null && !storeDTO.getStoreProfile().isEmpty()) {//이미지 새로 넣었고
            if (store.getStoreProfileMeta()!=null  && !store.getStoreProfileMeta().isEmpty()) {//기존 이미지가 있다면
                Path filePath = Paths.get("c:/data/hexastay", store.getStoreProfileMeta().substring(1));
                Files.deleteIfExists(filePath);//삭제
            }
            /*이미지 등록 절차...*/
            String fileOriginalName = storeDTO.getStoreProfile().getOriginalFilename();
            String fileFirstName = store.getStoreNum() + "_" + storeDTO.getStoreNum() + "_" + storeDTO.getStoreName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            storeDTO.setStoreProfileMeta("/store/" + fileName);
            Path uploadPath = Paths.get("c:/data/hexastay", "store/" + fileName);
            Path createPath = Paths.get("c:/data/hexastay", "store/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            storeDTO.getStoreProfile().transferTo(uploadPath.toFile());
            store.setStoreProfileMeta(storeDTO.getStoreProfileMeta());
        }
        store.setStoreName(storeDTO.getStoreName());
        store.setStorePhone(storeDTO.getStorePhone());
        store.setStoreStatus(storeDTO.getStoreStatus());
        store.setStoreCeoName(storeDTO.getStoreCeoName());
        store.setStoreAddress(storeDTO.getStoreAddress());
        store.setStoreLatitude(storeDTO.getStoreLatitude());
        store.setStoreLongitude(storeDTO.getStoreLongitude());
        store.setStoreWtmX(storeDTO.getStoreWtmX());
        store.setStoreWtmY(storeDTO.getStoreWtmY());
        store.setStoreCategory(storeDTO.getStoreCategory());
//        store.setStorePassword(storeDTO.getStorePassword());
        return store.getStoreNum();
    }

    @Override
    public List<StoreDTO> getAllList(AdminDTO adminDTO) {
        List<Store> storeList = new ArrayList<>();
        if(adminDTO.getAdminRole().equals("SUPERADMIN")) {
            storeList = storeRepository.findAll("alive");
            storeList.forEach(this::checkAndUpdateOrphanStatus);
            storeList = storeRepository.findAll("alive");
        }else if(adminDTO.getCompanyNum()!=null){
            storeList = storeRepository.findByCompanyNum(adminDTO.getCompanyNum(), "alive");
        }else if(adminDTO.getStoreNum()!=null){
            storeList.add(storeRepository.findById(adminDTO.getStoreNum()).orElseThrow(EntityNotFoundException::new));
        }else{
            return null;
        }
        List<StoreDTO> list = storeList.stream().map(data -> {
            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
            storeDTO.setCompanyName(data.getCompany().getCompanyName());
            return storeDTO;
        }).toList();
        return list;
    }

    @Override
    public Map<Long, String> getCompanyMap(AdminDTO adminDTO) {
        List<Store> storeList = storeRepository.findAll("alive");
        storeList.forEach(this::checkAndUpdateOrphanStatus);
        storeList = storeRepository.findAll("alive");

        List<Long> comNums = getCompanyNums(adminDTO);

        Map<Long, String> maps = storeList.stream()
                .map(Store::getCompany)
                .filter(company -> company != null && company.getCompanyNum() != null && company.getCompanyName() != null)
                .filter(comp-> comNums.contains(comp.getCompanyNum()))
                .collect(Collectors.toMap(
                        Company::getCompanyNum,
                        Company::getCompanyName,
                        (existing, replacement) -> existing, // 중복 키 무시
                        LinkedHashMap::new
                ));
        return maps;
    }

    @Override
    public List<Long> getCompanyNums(AdminDTO adminDTO) {
//        log.info(adminDTO);
        String role = adminDTO.getAdminRole().toUpperCase();
        List<Long> result = new ArrayList<>();
        if(role.equals("SUPERADMIN")){
            companyRepository.findAll().forEach(comp->result.add(comp.getCompanyNum()));
            return result;
        } else if (Arrays.asList("EXEC","HEAD","CREW").contains(role)) {
            Long parentCom = adminDTO.getCompanyNum();
            result.add(parentCom);
            List<Company> companies = companyRepository.findByCompanyParent(parentCom);
            companies.forEach(comp-> result.add(comp.getCompanyNum()));
            return result;
        }else if(Arrays.asList("GM","SV","AGENT").contains(role)){
//            log.info(adminDTO);
//            log.info(adminDTO.getCompanyNum());
            result.add(adminDTO.getCompanyNum());
            return result;
        }else {
            return null;
        }
    }


    //    /*
//     * 메소드명 : list
//     * 인수 값 : String status, Pageable
//     * 리턴 값 : Page<StoreDTO>
//     * 기  능 : 활성화 여부를 받아서 해당되는 상점들만 페이지 타입으로 반환합니다.
//     * */
//    @Override
//    public Page<StoreDTO> list(String status, Pageable pageable) {
//        Page<Store> storePage = storeRepository.findByStoreStatus(status, pageable);
//        Page<StoreDTO> storeDTOPage = storePage.map(data -> {
//            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
//            storeDTO.setCompanyName(data.getCompany().getCompanyName());
//            return storeDTO;
//        });
//        return storeDTOPage;
//    }
//    @Override
//    public List<StoreDTO> list(Long companyNum) {
//        List<Store> storeList = storeRepository.findByCompanyNum(companyNum);
//        List<StoreDTO> list = storeList.stream().map(data -> {
//            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
//            storeDTO.setCompanyName(data.getCompany().getCompanyName());
//            return storeDTO;
//        }).toList();
//        return list;
//    }
//    @Override
//    public Page<StoreDTO> list(Long companyNum, Pageable pageable) {
//        Page<Store> storeList = storeRepository.findByCompanyNum(companyNum, pageable);
//        storeList.forEach(this::checkAndUpdateOrphanStatus);
//        storeList = storeRepository.findByCompanyNum(companyNum, pageable);
//        Page<StoreDTO> list = storeList.map(data -> {
//            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
//            storeDTO.setCompanyName(data.getCompany().getCompanyName());
//            return storeDTO;
//        });
//        return list;
//    }
    @Override
    public Page<StoreDTO> searchlist(List<Long> companyNum,String searchType, String keyword, Pageable pageable, String... status) {
        Page<Store> storeList = storeRepository.listStoreSearch(companyNum, searchType, keyword, pageable, status);
        Page<StoreDTO> list = storeList.map(data -> {
            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
            storeDTO.setCompanyName(data.getCompany().getCompanyName());
            return storeDTO;
        });
//        log.info("서비스 들어옴 : ");
//        log.info(companyNum);
//        log.info(searchType);
//        log.info(keyword);
//        log.info(storeList.getSize());
//        storeList.forEach(log::info);
        return list;
    }


//    /*
//     * 메소드명 : list
//     * 인수 값 : Pageable
//     * 리턴 값 : Page<StoreDTO>
//     * 기  능 : 페이징 정보를 받아 StoreDTO들을 페이지 타입으로 반환
//     * */
//    @Override
//    public Page<StoreDTO> list(Pageable pageable) {
//        Page<Store> storePage = storeRepository.findAll(pageable);
//        storePage.forEach(this::checkAndUpdateOrphanStatus);
//        storePage = storeRepository.findAll(pageable);
//        Page<StoreDTO> storeDTOPage = storePage.map(data -> {
//            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
//            storeDTO.setCompanyName(data.getCompany().getCompanyName());
//            return storeDTO;
//        });
//        return storeDTOPage;
//    }
//    @Override
//    public Page<StoreDTO> clientlist(Pageable pageable) {
//
//        Page<Store> storeList = storeRepository.listStoreSearch(null, "", "", pageable);
//        Page<StoreDTO> list = storeList.map(data -> {
//            StoreDTO storeDTO = modelMapper.map(data, StoreDTO.class);
//            storeDTO.setCompanyName(data.getCompany().getCompanyName());
//            return storeDTO;
//        });
//        return list;
//    }
    @Override
    public Page<StoreDTO> clientlist(Long hotelroomNum, String type, String keyword, Pageable pageable, Locale locale) {
        log.info("StoreService: clientlist 다국어 서비스 진입 - hotelroomNum: {}, type: {}, keyword: {}, locale: {}",
                hotelroomNum, type, keyword, locale.toLanguageTag());

        Long companyNum = zzService.hotelroomNumToCompany(hotelroomNum).getCompanyNum();
        Page<Store> storeEntityPage = storeRepository.storeTypeSearch(companyNum, type, keyword, pageable);

        if (storeEntityPage.isEmpty()) {
            log.info("No stores found for companyNum: {}, type: {}, keyword: {}", companyNum, type, keyword);
            return Page.empty(pageable); // 빈 페이지 반환
        }

        // Store 엔티티를 StoreDTO로 매핑하면서 번역 적용
        Page<StoreDTO> storeDtoPage = storeEntityPage.map(storeEntity ->
                convertToStoreDTOWithTranslation(storeEntity, locale) // ★★★ 헬퍼 메소드 사용 ★★★
        );

        log.info("Returning Page of {} StoreDTOs for companyNum: {} (Locale: {})",
                storeDtoPage.getNumberOfElements(), companyNum, locale.toLanguageTag());
        return storeDtoPage;
    }


    /*
     * 메소드명 : delete
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Store 객체의 활성화 컬럼 데이터를 deleted 로 바꾼다.
     * */
    @Override
    public void delete(Long pk) {
        Store store = storeRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        store.setStoreStatus("deleted");
    }
    /*
     * 메소드명 : restore
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Store 객체의 활성화 컬럼 데이터를 alive 로 바꾼다.
     * */
    @Override
    public void restore(Long pk) {
        Store store = storeRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        store.setStoreStatus("alive");
    }

    /*
     * 메소드명 : validStoreAdmin
     * 인수 값 : AdminDTO adminDTO, StoreDTO storeDTO
     * 리턴 값 : boolean
     * 기  능 : StoreDTO의 storeNum과 adminDTO의 storeNum을 비교해 같으면 true 다르면 false 반환.
     * */
    @Override
    public boolean validStoreAdmin(AdminDTO adminDTO, StoreDTO storeDTO) {
        List<String> possibleRoles = Arrays.asList("SUPERADMIN","EXEC","HEAD","SV","PARTNER","GM");
        //상위 관리자라면 무조건 참.
        if(possibleRoles.contains(adminDTO.getAdminRole())){
            return true;
        }
        //스토어소속 직원이라면 현재 스토어가 자기 스토어인지 확인함
        else if (adminDTO.getStoreNum()!=null) {
            return adminDTO.getStoreNum().equals(storeDTO.getStoreNum());
        }
        //상위관리자도 아니고 스토어소속도 아니면 무조건 거짓.
        else {return false;}
    }

    @Override
    public void storeLiketoggle(Long storeNum, Member member) {
        StoreLike storeLike = storeLikeRepository.findByMemberAndStore_StoreNum(member,storeNum);
        if(storeLike==null){ //좋아요 안했음
            storeLike = new StoreLike();
            storeLike.setMember(member);
            storeLike.setStore(storeRepository.findById(storeNum).orElseThrow(EntityNotFoundException::new));
            storeLikeRepository.save(storeLike); //좋아요 추가
        }else { //좋아요 했음
            storeLikeRepository.delete(storeLike);//좋아요 취소
        }
    }

    @Override
    public long getStoreLikeCount(Long storeNum) {
        return storeLikeRepository.countByStore_StoreNum(storeNum);
    }

    @Override
    public boolean isLiked(Long storeNum, Member email) {
        return storeLikeRepository.existsByStore_StoreNumAndMember(storeNum, email);
    }

    //부모가 없으면서 "alive"상태라면, "deleted"상태로 바꿔준다.
    public void checkAndUpdateOrphanStatus(Store store) {
        if (store.getCompany() == null && !"deleted".equals(store.getStoreStatus())) {
            store.setStoreStatus("deleted");
            storeRepository.save(store);
        }
    }
}
