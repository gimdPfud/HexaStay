package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMenuOrderRepository extends JpaRepository<RoomMenuOrder, Long>{

    // 멤버를 참조하여 이메일을 찾는다.
    public Page<RoomMenuOrder> findByMember_MemberEmail(String email, Pageable pageable);

    List<RoomMenuOrder> findByMemberOrderByRegDateDesc(Member member);

}
