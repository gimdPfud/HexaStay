package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.dto.SettleDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.SettleService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

}
