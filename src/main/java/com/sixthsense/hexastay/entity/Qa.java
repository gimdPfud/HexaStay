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
public class Qa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qaNum")
    private Long qaNum; // 번호

    @Column(name = "qaTitle", nullable = false, length = 50)
    private String qaTitle; // 제목

    @Column(name = "qaContent", nullable = false, columnDefinition = "TEXT")
    private String qaContent; // 내용

    @Column(name = "qaWriter", nullable = false, length = 20)
    private String qaWriter; // 작성자

    @Column(nullable = false)
    private Boolean qaAnswered = false; // 답변 여부
//    @Column(name = "qaView", nullable = false, columnDefinition = "INT default 0")
//    private Integer qaView; // 조회수
    //가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;
}