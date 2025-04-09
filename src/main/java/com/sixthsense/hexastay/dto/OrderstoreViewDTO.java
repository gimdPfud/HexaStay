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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderstoreViewDTO {
    private Long orderstoreNum;
    private String orderstoreDate; //modifyDate
    private String orderstoreStatus;
    private List<OrderstoreitemDTO> orderstoreitemDTOList = new ArrayList<>();

    public OrderstoreViewDTO(Orderstore orders) {
        this.orderstoreNum = orders.getOrderstoreNum();
        this.orderstoreDate = orders.getModifyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderstoreStatus = orders.getOrderstoreStatus();
    }
    public void addOrderstoreitemDTOList(OrderstoreitemDTO orderstoreitemDTO){
        orderstoreitemDTOList.add(orderstoreitemDTO);
    }
}