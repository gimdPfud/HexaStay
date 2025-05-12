package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.util.exception.*;

public interface ReorderService {

    // 재주문 서비스 인터페이스 메소드
    void addPastOrderItemsToCart(Long orderNum, String userEmail)
            throws UserNotFoundException, OrderNotFoundException, UnauthorizedOrderAccessException,
            ProductNotFoundException, OptionNotFoundException, SoldOutException;
}
