package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewNum")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "memberNum")
    private Member member;
}

