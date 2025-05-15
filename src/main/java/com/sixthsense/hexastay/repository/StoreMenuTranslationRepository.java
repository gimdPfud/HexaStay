package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.StoreMenuTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreMenuTranslationRepository extends JpaRepository<StoreMenuTranslation, Long> {

    Optional<StoreMenuTranslation> findByStoreMenu_StoremenuNumAndLocale(Long storemenuNum, String locale);

}

