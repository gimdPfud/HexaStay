package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public interface SettleService {


    // 컴퍼니넘 소속 호텔 찾기
    public Page<RoomDTO> getSettleList(Long companyNum, Pageable pageable);


    // 스토어넘 소속 스토어 찾기
    Page<OrderstoreDTO> getSettleStoreList(Long storeNum, Pageable pageable);
}
