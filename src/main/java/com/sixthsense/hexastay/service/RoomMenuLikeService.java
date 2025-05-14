package com.sixthsense.hexastay.service;

public interface RoomMenuLikeService {

    public Integer roomMenuLike(Long roomMenuNum, String memberEmail);

    public Integer roomMenuLikeCancel(Long roomMenuNum, String memberEmail);

}
