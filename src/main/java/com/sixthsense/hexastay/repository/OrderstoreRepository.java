/***********************************************
 * 인터페이스명 : OrderstoreRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Orderstore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderstoreRepository extends JpaRepository<Orderstore, Long> {
    /*다가져오기*/
    @Query("select a from Orderstore a")
    public Page<Orderstore> findAll(Pageable pageable);

    @Query("select a from Orderstore a where a.orderstoreStatus='end'")
    public List<Orderstore> findAll();

//    Page<Orderstore> findByOrderstoreStoreNum(Long orderstoreStoreNum,Pageable pageable);
    Page<Orderstore> findByStore_StoreNum(Long storeNum,Pageable pageable);
    
    // 날짜 범위로 정산용 데이터 조회
    Page<Orderstore> findByStore_StoreNumAndCreateDateBetween(
            Long storeNum, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);
    
    /*페이지로 가져오기*/
//    Page<Orderstore> findByRoom_Member_MemberEmail (String email, Pageable pageable);
    Page<Orderstore> findByRoom_RoomNum (Long roomNum, Pageable pageable);
    /*리스트로 가져오기. 근데 주문내역이면........... 계속 나오지않나*/
//    List<Orderstore> findByRoom_Member_MemberEmail (String email);
    List<Orderstore> findByRoom_RoomNum (Long roomNum);

    /*스토어넘버로 가져오기*/
    /*fixme 친구가 만들어준거*/
    @Query("SELECT DISTINCT o FROM Orderstore o " +
            "JOIN o.orderstoreitemList oi " +
            "JOIN oi.storemenu sm " +
            "JOIN sm.store s " +
            "WHERE s.storeNum = :storeNum order by o.orderstoreNum DESC ")
    List<Orderstore> findByStoreNum(Long storeNum);

    @Query("SELECT DISTINCT o FROM Orderstore o " +
            "JOIN fetch o.orderstoreitemList oi " +
            "JOIN oi.storemenu sm " +
            "JOIN sm.store s " +
            "WHERE s.storeNum = :storeNum AND o.orderstoreStatus='paid'")
    List<Orderstore> selectPaidOrder(Long storeNum);
}