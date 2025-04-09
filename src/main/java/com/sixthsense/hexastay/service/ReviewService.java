package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface ReviewService {
    //목록
    public Page<ReviewDTO> reviewList(Pageable pageable);
    //등록
    public void reviewInsert(ReviewDTO reviewDTO);
    //수정
    public void reviewModify(ReviewDTO reviewDTO);
    //삭제
    public void reviewDelete(Long reviewNum);
    //좋아요
    public void reviewGood(Long reviewNum,Long memberNum);

    public ReviewDTO findById(Long reviewNum);
    //별점 평균 조회
    public Double reviewRatingAvg(Long hotelRoomNum);
    //베스트 리뷰 목록 (좋아요 순 또는 별점 순)
    public List<ReviewDTO> reviewBest();
}


