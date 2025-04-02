/***********************************************
 * 클래스명 : PaginationUtil
 * 기능 : 공용모듈(필요할때마다 사용가능한 메소드)
 * 작성자 : 김예령
 * 작성일 : 2025-03-25
 * 수정 : 2025-03-25
 * ***********************************************/
package com.sixthsense.hexastay.util;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class PaginationUtil {
    public static Map<String, Integer> Pagination(Page<?> page){
        //첫페이지(1), 이전(현재-1), 현재페이지, 이후(현재+1), 마지막페이지
        int currentPage = page.getNumber()+1;   //화면에 출력할 현재 페이지 번호(중앙)
        int totalPages = page.getTotalPages();  //전체 페이지 번호(마지막번호)
        int blockLimit = 5;                     //화면에 출력할 페이지번호의 개수. 처음 이전 11 12 13 ... 18 19 다음 마지막

        Map<String, Integer> pageInfo = new HashMap<>();
        int startPage = Math.max(1,(currentPage-blockLimit)/2);     //화면에 출력되는 시작페이지번호
        int endPage = Math.min(totalPages,startPage+blockLimit-1);  //화면에 출력되는 끝나는 페이지 번호
        int prevPage = Math.max(1,currentPage-1);                   //이전 페이지 번호 (현재가 0이면 존재불가능)
                            //1과 현재페이지-1 중 큰 수를 저장한다.
        int nextPage = Math.min(totalPages,currentPage+1);          //다음 페이지 번호 (마지막페이지보다 크면 존재불가능)
        int lastPage = totalPages;                                  //마지막 페이지 번호

        //계산한 페이지 정보를 저장해서 전달.
        pageInfo.put("startPage", startPage);
        pageInfo.put("endPage", endPage);
        pageInfo.put("prevPage", prevPage);
        pageInfo.put("nextPage", nextPage);
        pageInfo.put("lastPage", lastPage);
        pageInfo.put("currentPage", currentPage);
        pageInfo.put("totalPages", totalPages);
        pageInfo.put("blockLimit", blockLimit);

        return pageInfo;
    }
}
