/***********************************************
 * 인터페이스명 : StorecartitemRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Storecartitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StorecartitemRepository extends JpaRepository<Storecartitem, Long> {

    //장바구니에 상품이 있는지 확인하기.
    Storecartitem findByStorecart_StorecartNumAndStoremenu_StoremenuNum(Long cartNum, Long menuNum);
    Storecartitem findByStorecart_StorecartNumAndStoremenu_StoremenuNumAndStoremenuOptions(Long storecartNum, Long storemenuNum, String storemenuOptions);

    //장바구니의 아무 아이템 가져오기
    List<Storecartitem> findByStorecart_StorecartNum(Long storecartStorecartNum);


    //StorecartitemViewDTO 보여줄 쿼리 만들자...
    @Query("select new com.sixthsense.hexastay.dto.StorecartitemViewDTO(" +
            "sci.storecartitemNum, " +
            "sci.storemenu.storemenuName, " +
            "sci.storemenu.storemenuPrice, " +
            "sci.storecartitemCount, " +
            "sci.storemenu.storemenuImgMeta, " +
            "sci.storemenuOptions, " +
            "sci.optionPrice " +
            ") from Storecartitem sci where sci.storecart.room.roomNum=:roomNum")
    List<StorecartitemViewDTO> storeCartViewList(Long roomNum);

    @Query("select sum(s.storecartitemCount) from Storecartitem s where s.storecart.room.roomNum = ?1")
    Long countByStorecart_Room(Long roomNum);

}