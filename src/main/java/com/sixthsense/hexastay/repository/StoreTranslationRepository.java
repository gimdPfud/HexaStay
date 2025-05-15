package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.StoreTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreTranslationRepository extends JpaRepository<StoreTranslation, Long> {

    Optional<StoreTranslation> findByStore_StoreNumAndLocale(Long pk, String targetLocale);

}
