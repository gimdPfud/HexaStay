package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMenuLikeRepository extends JpaRepository<RoomMenuLike, Long> {

    Optional<RoomMenuLike> findByMember_MemberEmailAndRoomMenu(String memberEmail, RoomMenu roomMenu);

    Long countByRoomMenuAndRoomMenuLikedCheck(RoomMenu roomMenu, Boolean likedCheck);


}
