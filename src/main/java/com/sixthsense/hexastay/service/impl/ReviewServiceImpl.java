package com.sixthsense.hexastay.service.impl;
import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.ReviewDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.ReviewLike;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.ReviewLikeRepository;
import com.sixthsense.hexastay.repository.ReviewRepository;
import com.sixthsense.hexastay.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository; // (추가)
    private final ModelMapper modelMapper = new ModelMapper();

    //목록
    @Override
    public Page<ReviewDTO> reviewList(Pageable pageable) {
        log.info(pageable);
        Page<Review> result = reviewRepository.findAll(pageable);
        log.info(result);
        Page<ReviewDTO> dtos = result.map(review -> {
            ReviewDTO dto = modelMapper.map(review, ReviewDTO.class);
            dto.setReviewAuthor(review.getMember().getMemberName());
            return dto;
        });
        log.info(dtos);
        return dtos;
    }

    //등록
    @Override
    public void reviewInsert(ReviewDTO reviewDTO) {
        Member member = memberRepository.findById(reviewDTO.getMemberNum())
                .orElseThrow(EntityNotFoundException::new);
        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setReviewGood(0); // (추가) 좋아요 초기값
        reviewRepository.save(review);
    }

    //수정
    @Override
    public void reviewModify(ReviewDTO reviewDTO) {
        Review review = modelMapper.map(reviewDTO, Review.class);
        reviewRepository.save(review);
    }

    //삭제
    @Override
    public void reviewDelete(Long reviewNum) {
        reviewRepository.deleteById(reviewNum);
    }

    //리뷰 상세 조회
    @Override
    public ReviewDTO findById(Long reviewNum) {
        Review review = reviewRepository.findById(reviewNum)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음"));
        return modelMapper.map(review, ReviewDTO.class);
    }

    // (수정) 좋아요 기능 + 중복 방지
    @Override
    public void reviewGood(Long reviewNum, Long memberNum) {
        log.info("좋아요 기능 진입 - reviewNum: {}, memberNum: {}", reviewNum, memberNum);

        // (추가) 중복 체크
        boolean alreadyLiked = reviewLikeRepository.existsByReview_ReviewNumAndMember_MemberNum(reviewNum, memberNum);
        if (alreadyLiked) {
            log.info("이미 좋아요를 누른 회원입니다.");
            return;
        }

        Review review = reviewRepository.findById(reviewNum)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        review.setReviewGood(review.getReviewGood() + 1);
        reviewRepository.save(review);

        ReviewLike like = ReviewLike.builder()
                .review(review)
                .member(memberRepository.findById(memberNum).orElseThrow())
                .build();
        reviewLikeRepository.save(like);

        log.info("좋아요 완료 - 총 좋아요 수: {}", review.getReviewGood());
    }

    //별점 평균
    @Override
    public Double reviewRatingAvg(Long hotelRoomNum) {
        List<Review> reviews = reviewRepository.findByHotelRoom_HotelRoomNum(hotelRoomNum);
        if (reviews.isEmpty()) return 0.0;

        double avg = reviews.stream()
                .mapToInt(Review::getReviewRating)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0; // 소수점 1자리
    }

    //베스트 리뷰 (좋아요 많은 순)
    @Override
    public List<ReviewDTO> reviewBest() {
        List<Review> reviews = reviewRepository.findTop3ByOrderByReviewGoodDesc();
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }
}
