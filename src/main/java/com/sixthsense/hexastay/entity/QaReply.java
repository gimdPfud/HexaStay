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
public class QaReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyNum;

    @Column(nullable = false, length = 1000)
    private String replyContent;

    @Column(nullable = false, length = 20)
    private String replyWriter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_num")
    private Qa qa;
}
