package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.ReviewDTO;

public interface ReviewLikeService {
    public Integer toggleReviewLike(Long reviewNum, String memberEmail);
}
