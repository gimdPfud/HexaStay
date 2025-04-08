package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity
@Getter
@Setter
@ToString(exclude = "member")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@EntityListeners(AuditingEntityListener.class)
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faqNum;

    @Column(nullable = false, length = 100)
    private String faqTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String faqContent;

    @Column(nullable = false, length = 50)
    private String faqCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member;
}
