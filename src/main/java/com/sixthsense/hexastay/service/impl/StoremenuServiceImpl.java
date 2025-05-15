/***********************************************
 * 클래스명 : StoremenuServiceImpl
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.dto.StoremenuOptionDTO;
import com.sixthsense.hexastay.entity.StoreMenuTranslation;
import com.sixthsense.hexastay.entity.Storemenu;
import com.sixthsense.hexastay.entity.StoremenuOption;
import com.sixthsense.hexastay.repository.StoreMenuTranslationRepository;
import com.sixthsense.hexastay.repository.StoremenuRepository;
import com.sixthsense.hexastay.service.StoremenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
@Transactional
public class StoremenuServiceImpl implements StoremenuService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoremenuRepository storemenuRepository;
    private final StoreMenuTranslationRepository storeMenuTranslationRepository;

    /*
     * 메소드명 : insert
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 데이터베이스에 추가하고, 등록한 객체의 pk를 반환함.
     * */
    @Override
    public Long insert(StoremenuDTO storemenuDTO) throws IOException {
        storemenuDTO.setStoremenuStatus("alive");
        Storemenu storemenu = modelMapper.map(storemenuDTO, Storemenu.class);
        /*옵션 리스트 변환*/
        if (storemenuDTO.getStoremenuOptionDTOList() != null && !storemenuDTO.getStoremenuOptionDTOList().isEmpty()) {
            Storemenu finalStoremenu = storemenu;
            storemenu.setStoremenuOptionList(
                    storemenuDTO.getStoremenuOptionDTOList().stream()
                            .map(data -> {
                                StoremenuOption optionEntity = modelMapper.map(data, StoremenuOption.class);
                                optionEntity.setStoremenu(finalStoremenu);
                                return optionEntity;
                            })
                            .collect(Collectors.toList())
            );
        }
        storemenu = storemenuRepository.save(storemenu);

        if (storemenuDTO.getStoremenuImg() != null && !storemenuDTO.getStoremenuImg().isEmpty()) {
            String fileOriginalName = storemenuDTO.getStoremenuImg().getOriginalFilename();
            String fileFirstName = storemenu.getStoremenuNum() + "_" + storemenuDTO.getStoreNum();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            storemenuDTO.setStoremenuImgMeta("/store/menu/" + fileName);
            Path uploadPath = Paths.get("c:/data/hexastay", "store/menu/" + fileName);
            Path createPath = Paths.get("c:/data/hexastay", "store/menu/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            storemenuDTO.getStoremenuImg().transferTo(uploadPath.toFile());
        }
        storemenu.setStoremenuImgMeta(storemenuDTO.getStoremenuImgMeta());
        storemenuRepository.save(storemenu);
        return storemenu.getStoremenuNum();
    }

    /*
     * 메소드명 : read
     * 인수 값 : Long
     * 리턴 값 : StoremenuDTO
     * 기  능 : pk를 받아 해당하는 데이터를 찾아 반환함.
     * */
    @Override
    public StoremenuDTO read(Long pk, Locale locale) {
//        log.info("다국어 서비스 read 진입", pk, locale);

        Storemenu storemenu = storemenuRepository.findById(pk)
                .orElseThrow(() -> new EntityNotFoundException("Storemenu not found with id: " + pk)); // 예외 메시지 명확화

        StoremenuDTO data = modelMapper.map(storemenu, StoremenuDTO.class);

        // 옵션 리스트 처리 (기존 로직 유지)
        data.setStoremenuOptionDTOList(
                storemenu.getStoremenuOptionList().stream()
                        .filter(option -> option.getStoremenuOptionStatus().equals("alive")) // "alive" 상태인 옵션만 필터링
                        .map(StoremenuOptionDTO::new) // StoremenuOption을 StoremenuOptionDTO로 변환 (DTO 생성자 방식 사용)
                        .toList()
        );

        return data;
    }

    /*
     * 메소드명 : modify
     * 인수 값 : StoremenuDTO
     * 리턴 값 : Long
     * 기  능 : DTO를 받아 수정한 후, 수정한 객체의 pk를 반환한다.
     * */
    @Override
    public Long modify(StoremenuDTO storemenuDTO) throws IOException {
//        log.info("수정 서비스 진입 : "+storemenuDTO);
        Storemenu entity = storemenuRepository.findById(storemenuDTO.getStoremenuNum()).orElseThrow(EntityNotFoundException::new);
        if (storemenuDTO.getStoremenuImg() != null && !storemenuDTO.getStoremenuImg().isEmpty()) {
            if (entity.getStoremenuImgMeta() != null && !entity.getStoremenuImgMeta().isEmpty()) {
                Path filePath = Paths.get("c:/data/hexastay", entity.getStoremenuImgMeta());
                Files.deleteIfExists(filePath);
            }
            /*이미지 등록 절차...*/
            String fileOriginalName = storemenuDTO.getStoremenuImg().getOriginalFilename();
            String fileFirstName = entity.getStoremenuNum() + "_" + storemenuDTO.getStoreNum() + "_" + storemenuDTO.getStoremenuName();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            storemenuDTO.setStoremenuImgMeta("/store/menu/" + fileName);
            Path uploadPath = Paths.get("c:/data/hexastay", "store/menu/" + fileName);
            Path createPath = Paths.get("c:/data/hexastay", "store/menu/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            storemenuDTO.getStoremenuImg().transferTo(uploadPath.toFile());
            entity.setStoremenuImgMeta(storemenuDTO.getStoremenuImgMeta());
        }
        entity.setStoremenuContent(storemenuDTO.getStoremenuContent());
        entity.setStoremenuPrice(storemenuDTO.getStoremenuPrice());
        entity.setStoremenuCategory(storemenuDTO.getStoremenuCategory());
        entity.setStoremenuName(storemenuDTO.getStoremenuName());
        entity.setStoremenuStatus(storemenuDTO.getStoremenuStatus());

        /*옵션 리스트 변환*/
        if (storemenuDTO.getStoremenuOptionDTOList() != null &&
                !storemenuDTO.getStoremenuOptionDTOList().isEmpty()) {
            List<StoremenuOption> optionList = entity.getStoremenuOptionList();
            optionList.addAll(
                    storemenuDTO.getStoremenuOptionDTOList().stream()
                            .map(data -> {
                                StoremenuOption optionEntity = modelMapper.map(data, StoremenuOption.class);
                                optionEntity.setStoremenu(entity);
                                return optionEntity;
                            })
                            .collect(Collectors.toList())
            );
            entity.setStoremenuOptionList(optionList);
        }

        return entity.getStoremenuNum();
    }


    /*
     * 메소드명 : list
     * 인수 값 : Long storeNum, String status, Pageable pageable
     * 리턴 값 : Page<StoremenuDTO>
     * 기  능 : 활성화상태가 입력받은값과 동일하면서, fk가 해당되는 데이터만 page로 가져온다.
     * */


    // 공통 로직: Storemenu 엔티티를 StoremenuDTO로 변환하고 번역 적용
    private StoremenuDTO convertToDtoWithTranslation(Storemenu storemenuEntity, Locale locale) {
        if (storemenuEntity == null) {
            log.warn("convertToDtoWithTranslation: storemenuEntity is null, skipping.");
            return null;
        }
        StoremenuDTO menuDTO = modelMapper.map(storemenuEntity, StoremenuDTO.class);
        String targetLocale = locale.getLanguage();

        String originalCategoryKey = storemenuEntity.getStoremenuCategory();
        menuDTO.setStoremenuOriginalCategoryKey(originalCategoryKey);
        menuDTO.setStoremenuCategoryDisplay(getKoreanDisplayForCategoryKey(originalCategoryKey));

//        log.info("[CONVERT PRE-TRANSLATION] Item ID: {}, Locale: {}, DTO Name: '{}', DTO OrigKey: '{}', DTO DisplayCat: '{}'",
//                storemenuEntity.getStoremenuNum(), targetLocale, menuDTO.getStoremenuName(),
//                menuDTO.getStoremenuOriginalCategoryKey(), menuDTO.getStoremenuCategoryDisplay());

        if (!"ko".equals(targetLocale) && targetLocale != null && !targetLocale.isEmpty()) {
//            log.info("[CONVERT TRANSLATION-BLOCK] Item ID: {}, Attempting translation for locale: {}", storemenuEntity.getStoremenuNum(), targetLocale);
            try {
                Optional<StoreMenuTranslation> translationOpt = storeMenuTranslationRepository
                        .findByStoreMenu_StoremenuNumAndLocale(storemenuEntity.getStoremenuNum(), targetLocale);

                if (translationOpt.isPresent()) {
                    StoreMenuTranslation translation = translationOpt.get();
                    String transName = translation.getStoreMenuTranslationName();
                    String transContent = translation.getStoreMenuTranslationContent();
                    String transCategory = translation.getStoreMenuTranslationCategory();

//                    log.info("[CONVERT TRANSLATION-FOUND] Item ID: {}, Locale: {}, DB Name: '{}', DB Content: '{}', DB Category: '{}'",
//                            storemenuEntity.getStoremenuNum(), targetLocale,
//                            transName, transContent, transCategory);

                    // isValid 함수 대신 직접 null 및 empty 체크 후 설정
                    if (transName != null && !transName.isEmpty()) {
//                        log.info("[CONVERT SET-NAME] Item ID: {}, Setting name to: '{}'", storemenuEntity.getStoremenuNum(), transName);
                        menuDTO.setStoremenuName(transName);
                    } else {
                        log.warn("[CONVERT SET-NAME-SKIPPED] Item ID: {}, transName is null or empty. DTO Name remains: '{}'", storemenuEntity.getStoremenuNum(), menuDTO.getStoremenuName());
                    }

                    if (transContent != null && !transContent.isEmpty()) {
//                        log.info("[CONVERT SET-CONTENT] Item ID: {}, Setting content to: '{}'", storemenuEntity.getStoremenuNum(), transContent);
                        menuDTO.setStoremenuContent(transContent);
                    } else {
                        log.warn("[CONVERT SET-CONTENT-SKIPPED] Item ID: {}, transContent is null or empty. DTO Content remains: '{}'", storemenuEntity.getStoremenuNum(), menuDTO.getStoremenuContent());
                    }

                    if (transCategory != null && !transCategory.isEmpty()) {
//                        log.info("[CONVERT SET-CATEGORYDISPLAY] Item ID: {}, Setting categoryDisplay to: '{}'", storemenuEntity.getStoremenuNum(), transCategory);
                        menuDTO.setStoremenuCategoryDisplay(transCategory);
                    } else {
                        log.warn("[CONVERT SET-CATEGORYDISPLAY-SKIPPED] Item ID: {}, transCategory is null or empty. DTO CategoryDisplay remains: '{}'", storemenuEntity.getStoremenuNum(), menuDTO.getStoremenuCategoryDisplay());
                    }
                }
            } catch (Exception e) {
                log.error("[CONVERT TRANSLATION-ERROR] Item ID: {}, Error applying translation for locale: {}. Default values will be used.",
                        storemenuEntity.getStoremenuNum(), targetLocale, e);
            }
        }

//        log.info("[CONVERT FINAL-DTO] Item ID: {}, Final DTO: Name='{}', OrigKey='{}', DisplayCat='{}', Content='{}'",
//                storemenuEntity.getStoremenuNum(), menuDTO.getStoremenuName(),
//                menuDTO.getStoremenuOriginalCategoryKey(), menuDTO.getStoremenuCategoryDisplay(), menuDTO.getStoremenuContent());



        // 옵션 목록 매핑
        if (storemenuEntity.getStoremenuOptionList() != null) {
            menuDTO.setStoremenuOptionDTOList(
                    storemenuEntity.getStoremenuOptionList().stream()
                            .map(option -> {
                                StoremenuOptionDTO optionDTO = new StoremenuOptionDTO(option); // 또는 modelMapper.map(option, StoremenuOptionDTO.class);
                                return optionDTO;
                            })
                            .filter(optionDTO -> "alive".equals(optionDTO.getStoremenuOptionStatus())) // 실제 상태값에 맞게 조정
                            .collect(Collectors.toList())
            );
        } else {
            menuDTO.setStoremenuOptionDTOList(Collections.emptyList()); // 옵션이 null일 경우 빈 리스트 설정
        }
        return menuDTO;
    }

    private String getKoreanDisplayForCategoryKey(String categoryKey) {
        if (categoryKey == null) return "기타"; // 또는 적절한 기본값
        switch (categoryKey.toLowerCase()) {
            case "main": return "메인";
            case "side": return "사이드";
            case "drink": return "음료";
            case "plus": return "추가";
            // 다른 카테고리 키에 대한 매핑 추가
            default: return categoryKey; // 매핑되는 것이 없으면 키 자체를 반환
        }
    }

    @Override
    public List<StoremenuDTO> list(Long storeNum, String status, Locale locale) {
//        log.info("list(storeNum, status, locale) 호출 - storeNum: {}, status: {}, locale: {}", storeNum, status, locale.toLanguageTag());
        List<Storemenu> storemenuList = storemenuRepository.findByStoreStoreNumAndStoremenuStatus(storeNum, status);
        if (storemenuList.isEmpty()) return Collections.emptyList();
        return storemenuList.stream()
                .map(entity -> convertToDtoWithTranslation(entity, locale))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoremenuDTO> list(Long storeNum, Locale locale) {
//        log.info("list(storeNum, locale) 호출 - storeNum: {}, locale: {}", storeNum, locale.toLanguageTag());

        List<Storemenu> storemenuList = storemenuRepository.findByStoreStoreNumAndStoremenuStatus(storeNum, "alive");

        if (storemenuList.isEmpty()) return Collections.emptyList();
        return storemenuList.stream()
                .map(entity -> convertToDtoWithTranslation(entity, locale))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoremenuDTO> list(Long storeNum, String category, String status, Locale locale) {
//        log.info("list(storeNum, category, status, locale) 호출 - storeNum: {}, category: {}, status: {}, locale: {}", storeNum, category, status, locale.toLanguageTag());
        List<Storemenu> storemenuList = storemenuRepository.findCateg(storeNum, category, status);
        if (storemenuList.isEmpty()) return Collections.emptyList();
        return storemenuList.stream()
                .map(entity -> convertToDtoWithTranslation(entity, locale))
                .collect(Collectors.toList());
    }


    /*
     * 메소드명 : delete
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 deleted로 바꾼다.
     * */
    @Override
    public Long delete(Long pk) {
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuStatus("deleted");
        return storemenu.getStore().getStoreNum();
    }

    /*
     * 메소드명 : delete
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 deleted로 바꾼다.
     * */
    @Override
    public Long soldout(Long pk) {
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuStatus("soldout");
        return storemenu.getStore().getStoreNum();
    }


    /*
     * 메소드명 : restore
     * 인수 값 : Long
     * 리턴 값 : void
     * 기  능 : pk를 받아 해당하는 Storemenu 객체의 활성화 컬럼 데이터를 alive로 바꾼다.
     * */
    @Override
    public Long restore(Long pk) {
        Storemenu storemenu = storemenuRepository.findById(pk).orElseThrow(EntityNotFoundException::new);
        storemenu.setStoremenuStatus("alive");
        return storemenu.getStore().getStoreNum();
    }
}
