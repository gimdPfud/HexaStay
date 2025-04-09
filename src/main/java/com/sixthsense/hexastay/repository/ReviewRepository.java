package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // (추가) 특정 방에 대한 모든 리뷰
    List<Review> findByHotelRoom_HotelRoomNum(Long hotelRoomNum);
    // (추가) 좋아요 순으로 상위 3개
    List<Review> findTop3ByOrderByReviewGoodDesc();
}

