/***********************************************
 * 클래스명 : OrderstoreViewDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-09
 * 수정 : 2025-04-09
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Orderstore;
import com.sixthsense.hexastay.entity.Orderstoreitem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderstoreViewDTO {
    private Long orderstoreNum;
    private LocalDateTime orderstoreDate; //modifyDate
    private String orderstoreStatus;
    private int orderstoreFinalPrice;
    private String orderstoreStoreName;
    private String orderstoreFirstItemName;
    private String orderstoreMessage;
    private String imgName;             //이미지
    private List<OrderstoreitemDTO> orderstoreitemDTOList = new ArrayList<>();

    public OrderstoreViewDTO(Orderstore orders) {

        this.orderstoreDate = orders.getModifyDate();
//        현재시간과 비교해서...날짜
//        if(this.orderstoreDate.getYear() == now.getYear()){
//            this.formatDate = orderstoreDate.format(DateTimeFormatter.ofPattern("MM-dd"));
//        } else {
//            this.formatDate = orderstoreDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        }

        this.orderstoreNum = orders.getOrderstoreNum();
        this.orderstoreFinalPrice = orders.getOrderstoreitemList().stream().filter(Objects::nonNull)
                .mapToInt(Orderstoreitem::getOrderstoreitemTotalPrice)
                .sum();
        this.orderstoreStatus = orders.getOrderstoreStatus();
        this.orderstoreStoreName = orders.getOrderstoreitemList().getFirst().getStoremenu().getStore().getStoreName();
        this.orderstoreFirstItemName = orders.getOrderstoreitemList().getFirst().getStoremenu().getStoremenuName();
        this.imgName = orders.getStore().getStoreProfileMeta();
    }
    public void addOrderstoreitemDTOList(OrderstoreitemDTO orderstoreitemDTO){
        orderstoreitemDTOList.add(orderstoreitemDTO);
    }
}