package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.OrderstoreDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
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
import java.util.List;
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
        Page<OrderstoreDTO> orderstoreDTOList = orderstoreList.map(order -> modelMapper.map(order, OrderstoreDTO.class));
        return orderstoreDTOList;
    }
    
    // 날짜 범위로 정산용 (스토어) 데이터 조회
    @Override
    public Page<OrderstoreDTO> getSettleStoreListByDateRange(Long storeNum, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // 시작일과 종료일을 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // 날짜 범위와 스토어 번호로 필터링하여 조회
        // 참고: 실제 Repository에 해당 메서드가 구현되어 있어야 함
        Page<Orderstore> orderstoreList = orderstoreRepository.findByStore_StoreNumAndCreateDateBetween(
                storeNum, startDateTime, endDateTime, pageable);
        
        // DTO로 변환
        Page<OrderstoreDTO> orderstoreDTOList = orderstoreList.map(order -> 
                modelMapper.map(order, OrderstoreDTO.class));
        
        return orderstoreDTOList;
    }
}
