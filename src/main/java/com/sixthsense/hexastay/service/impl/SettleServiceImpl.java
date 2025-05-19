package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.OrderstoreDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.dto.SettleDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Orderstore;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.SettleService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@Log4j2
@Service
@RequiredArgsConstructor
public class SettleServiceImpl implements SettleService {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final RoomRepository roomRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final OrderstoreRepository orderstoreRepository;
    private final SettleRepository settleRepository;


    // 정산용
    @Override
    public Page<RoomDTO> getSettleList(Long companyNum, Pageable pageable) {
        List<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum);
        List<Long> hotelRoomNums = hotelRoomList.stream()
                .map(HotelRoom::getHotelRoomNum)
                .collect(Collectors.toList());
        Page<Room> roomList = roomRepository.findByHotelRoom_HotelRoomNumIn(hotelRoomNums, pageable);
        Page<RoomDTO> roomDTOList = roomList.map(room -> {
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomDTO(modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class));
            }
            if (room.getMember() != null) {
                roomDTO.setMemberDTO(modelMapper.map(room.getMember(), MemberDTO.class));
            }
            return roomDTO;
        });

        return roomDTOList;
    }
    
    // 날짜 범위로 정산용 데이터 조회
    @Override
    public Page<RoomDTO> getSettleListByDateRange(Long companyNum, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // 시작일과 종료일을 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // 해당 회사의 호텔룸 목록 조회
        List<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum);
        List<Long> hotelRoomNums = hotelRoomList.stream()
                .map(HotelRoom::getHotelRoomNum)
                .collect(Collectors.toList());
        
        // 날짜 범위와 호텔룸 목록으로 필터링하여 조회
        // 참고: 실제 Repository에 해당 메서드가 구현되어 있어야 함
        Page<Room> roomList = roomRepository.findByHotelRoom_HotelRoomNumInAndCreateDateBetween(
                hotelRoomNums, startDateTime, endDateTime, pageable);
        
        // DTO로 변환
        Page<RoomDTO> roomDTOList = roomList.map(room -> {
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomDTO(modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class));
            }
            if (room.getMember() != null) {
                roomDTO.setMemberDTO(modelMapper.map(room.getMember(), MemberDTO.class));
            }
            return roomDTO;
        });

        return roomDTOList;
    }

    @Override
    public List<RoomDTO> getAllSettleList(Long companyNum) {
        List<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum);
        List<Long> hotelRoomNums = hotelRoomList.stream()
                .map(HotelRoom::getHotelRoomNum)
                .collect(Collectors.toList());
        
        List<Room> roomList = roomRepository.findByHotelRoom_HotelRoomNumIn(hotelRoomNums);
        
        return roomList.stream().map(room -> {
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomDTO(modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class));
            }
            if (room.getMember() != null) {
                roomDTO.setMemberDTO(modelMapper.map(room.getMember(), MemberDTO.class));
            }
            return roomDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getAllSettleListByDateRange(Long companyNum, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum);
        List<Long> hotelRoomNums = hotelRoomList.stream()
                .map(HotelRoom::getHotelRoomNum)
                .collect(Collectors.toList());
        
        List<Room> roomList = roomRepository.findByHotelRoom_HotelRoomNumInAndCreateDateBetween(
                hotelRoomNums, startDateTime, endDateTime);
        
        return roomList.stream().map(room -> {
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            if (room.getHotelRoom() != null) {
                roomDTO.setHotelRoomDTO(modelMapper.map(room.getHotelRoom(), HotelRoomDTO.class));
            }
            if (room.getMember() != null) {
                roomDTO.setMemberDTO(modelMapper.map(room.getMember(), MemberDTO.class));
            }
            return roomDTO;
        }).collect(Collectors.toList());
    }

    // 정산용 (스토어)
    @Override
    public Page<OrderstoreDTO> getSettleStoreList(Long storeNum, Pageable pageable) {
        Page<Orderstore> orderstoreList = orderstoreRepository.findByStore_StoreNum(storeNum, pageable);
        Page<OrderstoreDTO> orderstoreDTOList = orderstoreList.map(order -> {
            OrderstoreDTO dto = modelMapper.map(order, OrderstoreDTO.class);
            // 메뉴 정보 설정 (모든 메뉴명 리스트)
            // if (order.getOrderstoreitemList() != null && !order.getOrderstoreitemList().isEmpty()) {
            //     String menuNames = order.getOrderstoreitemList().stream()
            //         .map(item -> item.getStoremenu().getStoremenuName())
            //         .collect(java.util.stream.Collectors.joining(", "));
            //     dto.setStoreMenuName(menuNames);
            // }
            // 예약자 정보 설정
            // if (order.getRoom() != null && order.getRoom().getMember() != null) {
            //     dto.setMemberName(order.getRoom().getMember().getMemberName());
            // }
            return dto;
        });
        return orderstoreDTOList;
    }
    
    // 날짜 범위로 정산용 (스토어) 데이터 조회
    @Override
    public Page<OrderstoreDTO> getSettleStoreListByDateRange(Long storeNum, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Page<Orderstore> orderstoreList = orderstoreRepository.findByStore_StoreNumAndCreateDateBetween(
            storeNum, startDateTime, endDateTime, pageable);
            
        Page<OrderstoreDTO> orderstoreDTOList = orderstoreList.map(order -> {
            OrderstoreDTO dto = modelMapper.map(order, OrderstoreDTO.class);
            // 메뉴 정보 설정 (모든 메뉴명 리스트)
            // if (order.getOrderstoreitemList() != null && !order.getOrderstoreitemList().isEmpty()) {
            //     String menuNames = order.getOrderstoreitemList().stream()
            //         .map(item -> item.getStoremenu().getStoremenuName())
            //         .collect(java.util.stream.Collectors.joining(", "));
            //     dto.setStoreMenuName(menuNames);
            // }
            // 예약자 정보 설정
            // if (order.getRoom() != null && order.getRoom().getMember() != null) {
            //     dto.setMemberName(order.getRoom().getMember().getMemberName());
            // }
            return dto;
        });
        return orderstoreDTOList;
    }

    // 스토어 정산 통계 데이터 조회
    @Override
    public Map<String, Object> getStoreSettleStatistics(Long storeNum, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<Orderstore> orderstoreList = orderstoreRepository.findByStore_StoreNumAndCreateDateBetween(
            storeNum, startDateTime, endDateTime);
            
        int totalSales = 0;
        int totalSettlement = 0;
        int totalOrders = orderstoreList.size();
        
        for (Orderstore order : orderstoreList) {
            // orderstoreitemList의 각 항목의 가격을 합산
            int orderTotal = order.getOrderstoreitemList().stream()
                .mapToInt(item -> item.getOrderstoreitemPrice() * item.getOrderstoreitemAmount())
                .sum();
                
            totalSales += orderTotal;
            // 5% 수수료 공제
            totalSettlement += (int)(orderTotal * 0.95);
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSales", totalSales);
        statistics.put("totalSettlement", totalSettlement);
        statistics.put("totalOrders", totalOrders);
        
        return statistics;
    }

    @Override
    public List<SettleDTO> getStoreSettleList(Long storeNum) {
        List<Orderstore> orderstoreList = settleRepository.findByStoreNum(storeNum);
        return orderstoreList.stream()
            .map(orderstore -> {
                long totalSales = orderstore.getOrderstoreitemList().stream()
                    .mapToLong(item -> item.getOrderstoreitemPrice() * item.getOrderstoreitemAmount())
                    .sum();
                // 비용은 매출의 70%로 가정
                long totalCost = (long)(totalSales * 0.7);
                
                // 메뉴 정보 설정
                String menuNames = orderstore.getOrderstoreitemList().stream()
                    .map(item -> item.getStoremenu().getStoremenuName())
                    .collect(Collectors.joining(", "));
                
                // 수량 계산
                int quantity = orderstore.getOrderstoreitemList().stream()
                    .mapToInt(item -> item.getOrderstoreitemAmount())
                    .sum();
                
                // 단가 계산 (평균)
                long unitPrice = quantity > 0 ? totalSales / quantity : 0;
                
                SettleDTO settleDTO = SettleDTO.builder()
                    .settleNum(orderstore.getOrderstoreNum())
                    .settleDate(orderstore.getCreateDate().toLocalDate())
                    .settleSales(totalSales)
                    .settleCost(totalCost)
                    .settleProfit(totalSales - totalCost)
                    .settleStatus(orderstore.getOrderstoreStatus())
                    .storeNum(storeNum)
                    .menuNames(menuNames)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();
                
                // 회원 정보 설정
                if (orderstore.getRoom() != null && orderstore.getRoom().getMember() != null) {
                    MemberDTO memberDTO = modelMapper.map(orderstore.getRoom().getMember(), MemberDTO.class);
                    settleDTO.setMemberDTO(memberDTO);
                }
                
                return settleDTO;
            })
            .collect(Collectors.toList());
    }
}
