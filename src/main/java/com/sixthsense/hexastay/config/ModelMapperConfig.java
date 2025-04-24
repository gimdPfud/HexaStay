package com.sixthsense.hexastay.config;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Salaries;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Room.class, RoomDTO.class)
                .addMappings(mapper -> mapper.map(Room::getHotelRoom, RoomDTO::setHotelRoomDTO));

        modelMapper.typeMap(Salaries.class, SalariesDTO.class)
                .addMappings(mapper -> mapper.map(Salaries::getAdmin, SalariesDTO::setAdminDTO));

        return modelMapper;
    }
}
