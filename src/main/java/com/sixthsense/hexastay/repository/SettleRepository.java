package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Orderstore;
import com.sixthsense.hexastay.entity.Salaries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SettleRepository extends JpaRepository<Salaries, Integer> {

    Page<Salaries> findByAdmin_AdminNum(Long adminNum, Pageable pageable);
    Page<Salaries> findByAdmin_Company_CompanyNum(Long companyNum, Pageable pageable);
    Page<Salaries> findByAdmin_Store_StoreNum(Long storeNum, Pageable pageable);

    @Query("SELECT o FROM Orderstore o WHERE o.store.storeNum = :storeNum")
    List<Orderstore> findByStoreNum(@Param("storeNum") Long storeNum);

}
