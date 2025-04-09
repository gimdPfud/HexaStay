package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.ReviewLike;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.ReviewLikeRepository;
import com.sixthsense.hexastay.repository.ReviewRepository;
import com.sixthsense.hexastay.service.ReviewLikeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    // (추가) 리뷰 좋아요 토글 처리
    @Override
    public Integer toggleReviewLike(Long reviewNum, String memberEmail) {
        log.info("좋아요 토글 요청: reviewNum={}, memberId={}", reviewNum, memberEmail);

        Member findmember = memberRepository.findByMemberEmail(memberEmail);
        Review findreview = reviewRepository.findById(reviewNum).orElseThrow(EntityNotFoundException::new);

        // (중복 체크)
        boolean exists = reviewLikeRepository.existsByReview_ReviewNumAndMember_MemberNum(reviewNum, findmember.getMemberNum());
        if (exists) {
            log.info("이미 좋아요 누른 리뷰입니다.");
            return -1;
        }

        // (좋아요 기록 저장)
        ReviewLike like = ReviewLike.builder()
                .review(findreview)
                .member(findmember)
                .build();
        reviewLikeRepository.save(like);

        // (리뷰 좋아요 수 증가)
        Optional<Review> optionalReview = reviewRepository.findById(reviewNum);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            int currentCount = review.getReviewGood();
            review.setReviewGood(currentCount + 1);
            reviewRepository.save(review);
            log.info("리뷰 좋아요 수 증가 완료. 현재 수: {}", review.getReviewGood());
            return review.getReviewGood();
        } else {
            throw new RuntimeException("리뷰 정보 없음: reviewNum = " + reviewNum);
        }
    }
}
