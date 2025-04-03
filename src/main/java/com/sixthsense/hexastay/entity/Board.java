package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardNum")
    private Long boardNum; // 번호

    @Column(name = "boardTitle", nullable = false, length = 50)
    private String boardTitle; // 제목

    @Column(name = "boardContent", nullable = false, columnDefinition = "TEXT")
    private String boardContent; // 내용

    @Column(name = "boardWriter", nullable = false, length = 20)
    private String boardWriter; // 작성자

    @Column(name = "boardView", nullable = false, columnDefinition = "INT default 0")
    private Integer boardView; // 조회수
    //가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;

}