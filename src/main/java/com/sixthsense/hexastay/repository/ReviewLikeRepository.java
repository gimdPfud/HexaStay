package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    // (추가) 좋아요 중복 체크
    boolean existsByReview_ReviewNumAndMember_MemberNum (Long reviewNum, Long memberNum);
}

