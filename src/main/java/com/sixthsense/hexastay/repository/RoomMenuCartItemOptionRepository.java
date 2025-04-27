package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenuCartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMenuCartItemOptionRepository extends JpaRepository<RoomMenuCartItemOption, Long> {


    List<RoomMenuCartItemOption> findByRoomMenuCartItem_RoomMenuCartItemNum(Long roomMenuCartItemNum);
}
