package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.OrderstoreDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SettleService {

    //정산용

    // 컴퍼니넘 소속 호텔 찾기
    public Page<RoomDTO> getSettleList(Long companyNum, Pageable pageable);
    
    // 날짜 범위로 컴퍼니넘 소속 호텔 찾기
    public Page<RoomDTO> getSettleListByDateRange(Long companyNum, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 모든 정산 데이터 가져오기
    public List<RoomDTO> getAllSettleList(Long companyNum);
    
    // 날짜 범위로 모든 정산 데이터 가져오기
    public List<RoomDTO> getAllSettleListByDateRange(Long companyNum, LocalDate startDate, LocalDate endDate);

    // 스토어넘 소속 스토어 찾기
    Page<OrderstoreDTO> getSettleStoreList(Long storeNum, Pageable pageable);
    
    // 날짜 범위로 스토어넘 소속 스토어 찾기
    Page<OrderstoreDTO> getSettleStoreListByDateRange(Long storeNum, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
