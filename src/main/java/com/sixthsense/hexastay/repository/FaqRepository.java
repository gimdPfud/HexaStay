package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}
