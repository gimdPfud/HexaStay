package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomMenuCartRepository extends JpaRepository<RoomMenuCart, Long> {
    @Query("select r from RoomMenuCart r")
    public Page<RoomMenuCart> findAll(Pageable pageable);

    public RoomMenuCart findByMember_MemberEmail(String email);

    // RoomMenuCartRepository
    Optional<RoomMenuCart> findByMember(Member member);

}
