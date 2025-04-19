package com.sixthsense.hexastay.config;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.entity.Room;
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

        return modelMapper;
    }
}
