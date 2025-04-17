package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenuTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomMenuTranslationRepository extends JpaRepository<RoomMenuTranslation, Long> {

    Optional<RoomMenuTranslation> findByRoomMenu_RoomMenuNumAndLocale(Long roomMenuId, String locale);

}
