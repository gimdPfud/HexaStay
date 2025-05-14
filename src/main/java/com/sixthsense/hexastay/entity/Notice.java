package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeNum")
    private Long noticeNum; // 번호

    @Column(name = "noticeTitle", nullable = false, length = 50)
    private String noticeTitle; // 제목

    @Column(name = "noticeContent", nullable = false, columnDefinition = "TEXT")
    private String noticeContent; // 내용

//    @Column(name = "noticeWriter", nullable = false, length = 20)
//    private String noticeWriter; // 작성자

    @Column(name = "noticeView", nullable = false, columnDefinition = "INT default 0")
    private Integer noticeView; // 조회수
    //가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;
}