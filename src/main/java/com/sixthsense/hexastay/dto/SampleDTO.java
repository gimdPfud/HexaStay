/***********************************************
 * 클래스명 : SampleDTO
 * 기능 : 샘플DTO입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27 DTO생성,김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleDTO {
    private Long sampleNum;
    private String sampleColumn;
    private LocalDateTime sampleRegDate;
}
