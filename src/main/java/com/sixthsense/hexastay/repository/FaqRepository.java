package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByFaqCategory(String category);
}
