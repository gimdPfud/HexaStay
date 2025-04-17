package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import lombok.*;

/***********************************************
 * 클래스명 : RoomMenuLikeDTO
 * 기능 : 룸 메뉴에 대한 사용자의 좋아요/싫어요 정보를 담는 DTO 클래스
 * - 좋아요/싫어요 식별 번호, 사용자 정보(Member), 룸 메뉴 정보(RoomMenu)를 포함
 * - roomMenuLikedCheck 필드를 통해 좋아요(true) 또는 싫어요(false) 상태를 표시
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-10
 * 수정일 : -
 * ***********************************************/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoomMenuLikeDTO {

    private Long roomMenuLikeNum;

    private Member member;

    private RoomMenu roomMenu;

    private Boolean roomMenuLikedCheck; // true = 좋아요, false = 싫어요

}
