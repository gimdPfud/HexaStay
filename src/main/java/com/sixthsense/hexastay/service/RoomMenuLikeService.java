package com.sixthsense.hexastay.service;

import java.util.Map;

public interface RoomMenuLikeService {

    public Integer roomMenuLike(Long roomMenuNum, String memberEmail);

    public Integer roomMenuLikeCancel(Long roomMenuNum, String memberEmail);

    public Integer roomMenuDisLike(Long roomMenuNum, String memberEmail);

    public Integer roomMenuDisLikeCancel(Long roomMenuNum, String memberEmail);




}
