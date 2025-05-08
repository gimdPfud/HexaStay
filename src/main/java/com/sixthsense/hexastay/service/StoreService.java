/***********************************************
 * 인터페이스명 : Storemenu
 * 기능 : 외부 업체의 처리를 담당하는 서비스 (인터페이스)
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface StoreService {
    /*등록*/
    Long insert(StoreDTO storeDTO) throws IOException;

    /*읽기*/
    StoreDTO read(Long pk, Locale locale);

    /*수정*/
    Long modify(StoreDTO storeDTO) throws IOException;

    /*목록 : 전부 가져오기*/
    List<StoreDTO> getAllList();
    Map<Long, String> getCompanyMap();
    /*목록
    * 활성화된 외부업체만 목록으로 보여주기
    * 모든 외부 업체 목록 보여주기*/
    Page<StoreDTO> list(String status, Pageable pageable);
    Page<StoreDTO> list(Pageable pageable);
    List<StoreDTO> list(Long companyNum);
    Page<StoreDTO> list(Long companyNum, Pageable pageable);
    Page<StoreDTO> searchlist(Long companyNum, String searchType, String keyword, Pageable pageable, String... status);

    Page<StoreDTO> clientlist(Pageable pageable);
    Page<StoreDTO> clientlist(Long hotelroomNum, String type, String keyword, Pageable pageable, Locale locale);

    /*삭제: 활성화->비활성화 바꾸기*/
    void delete(Long pk);
    void restore(Long pk);

    //검증하기 admin과 store의 주인!!...
    boolean validStoreAdmin(AdminDTO adminDTO, StoreDTO storeDTO);

    /*좋아요... 좋아요한 스토어의 좋아요 수 리턴*/
    void storeLiketoggle(Long storeNum, Member member);
    long getStoreLikeCount(Long storeNum);
    boolean isLiked(Long storeNum, Member member);
}
