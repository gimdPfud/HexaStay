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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface StoreService {
    /*등록*/
    public Long insert(StoreDTO storeDTO) throws IOException;

    /*읽기*/
    public StoreDTO read(Long pk);

    /*수정*/
    public Long modify(StoreDTO storeDTO) throws IOException;

    /*추가목록 : 전부 가져오기*/
    public List<StoreDTO> getAllList();
    /*목록?
    * 활성화된 외부업체만 목록으로 보여주기
    * 모든 외부 업체 목록 보여주기*/
    public Page<StoreDTO> list(String status, Pageable pageable);
    public Page<StoreDTO> list(Pageable pageable);
    public List<StoreDTO> list(Long companyNum);
    public Page<StoreDTO> list(Long companyNum, Pageable pageable);

    /*todo 목록인데 리뷰가 있는 목록. 근데 리뷰별점을 넣을지말지 고민중...*/
    public Page<StoreDTO> clientlist(Pageable pageable);

    /*삭제: 활성화->비활성화 바꾸기*/
    public void delete(Long pk);
    public void restore(Long pk);

    //검증하기 admin과 store의 주인!!...
    public boolean validStoreAdmin(AdminDTO adminDTO, StoreDTO storeDTO);
}
